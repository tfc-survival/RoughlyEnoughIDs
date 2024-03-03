package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.ICubicWorldInternal;
import io.github.opencubicchunks.cubicchunks.core.lighting.ILightingManager;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.ClientHeightMap;
import io.github.opencubicchunks.cubicchunks.core.world.IColumnInternal;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Pseudo
@Mixin(targets = "io.github.opencubicchunks.cubicchunks.core.network.WorldEncoder")
public class MixinWorldEncoder {
    @Overwrite
    static void encodeCubes(PacketBuffer out, Collection<Cube> cubes) {
        // write first all the flags, then all the block data, then all the light data etc for better compression

        // 1. emptiness
        cubes.forEach(cube -> {
            byte flags = 0;
            if(cube.isEmpty())
                flags |= 1;
            if(cube.getStorage() != null)
                flags |= 2;
            if(((INewCube) cube).getBiomeArray() != null)
                flags |= 4;
            out.writeByte(flags);
        });

        // 2. block IDs and metadata
        cubes.forEach(cube -> {
            if (!cube.isEmpty()) {
                //noinspection ConstantConditions
                cube.getStorage().getData().write(out);
            }
        });

        // 3. block light
        cubes.forEach(cube -> {
            if (cube.getStorage() != null) {
                out.writeBytes(cube.getStorage().getBlockLight().getData());
            }
        });

        // 4. sky light
        cubes.forEach(cube -> {
            if (cube.getStorage() != null && cube.getWorld().provider.hasSkyLight()) {
                out.writeBytes(cube.getStorage().getSkyLight().getData());
            }
        });

        // 5. heightmap and bottom-block-y. Each non-empty cube has a chance
        // to update this data.
        // trying to keep track of when it changes would be complex, so send
        // it wil all cubes
        cubes.forEach(cube -> {
            if (!cube.isEmpty()) {
                ((IColumnInternal) cube.getColumn()).writeHeightmapDataForClient(out);
            }
        });
        
        // 6. biomes
        cubes.forEach(cube -> {
            int[] biomes = ((INewCube) cube).getBiomeArray();
            if (biomes != null)
                for (int biome : biomes)
                    out.writeInt(biome);
        });
    }

    @Overwrite
    static void encodeColumn(PacketBuffer out, Chunk column) {
        // 1. biomes
        int[] biomes = ((INewChunk) column).getIntBiomeArray();
        //Utils.LOGGER.info("current biome: {}", biomes[128]);
        for (int i = 0; i < 256; i++)
            out.writeInt(biomes[i]);

        ((IColumnInternal) column).writeHeightmapDataForClient(out);
    }

    @Overwrite
    static void decodeColumn(PacketBuffer in, Chunk column) {
        // 1. biomes
        int[] biomes = new int[256];
        for (int i = 0; i < 256; i++)
        {
            biomes[i] = in.readInt();
        }
        ((INewChunk)column).setIntBiomeArray(biomes);

        if (in.readableBytes() > 0) {
            ((IColumnInternal) column).loadClientHeightmapData(in);
        }
    }

    @Overwrite
    static void decodeCube(PacketBuffer in, List<Cube> cubes) {
        cubes.stream().filter(Objects::nonNull).forEach(Cube::setClientCube);

        // 1. emptiness
        boolean[] isEmpty = new boolean[cubes.size()];
        boolean[] hasStorage = new boolean[cubes.size()];
        boolean[] hasCustomBiomeMap = new boolean[cubes.size()];

        for (int i = 0; i < cubes.size(); i++) {
            byte flags = in.readByte();
            isEmpty[i] = (flags & 1) != 0 || cubes.get(i) == null;
            hasStorage[i] = (flags & 2) != 0 && cubes.get(i) != null;
            hasCustomBiomeMap[i] = (flags & 4) != 0 && cubes.get(i) != null;
        }

        for (int i = 0; i < cubes.size(); i++) {
            if (hasStorage[i]) {
                Cube cube = cubes.get(i);
                ExtendedBlockStorage storage = new ExtendedBlockStorage(Coords.cubeToMinBlock(cube.getY()),
                        cube.getWorld().provider.hasSkyLight());
                cube.setStorageFromSave(storage);
            }
        }

        // 2. Block IDs and metadata
        for (int i = 0; i < cubes.size(); i++) {
            if (!isEmpty[i]) {
                //noinspection ConstantConditions
                cubes.get(i).getStorage().getData().read(in);
            }
        }

        // 3. block light
        for (int i = 0; i < cubes.size(); i++) {
            if (hasStorage[i]) {
                //noinspection ConstantConditions
                byte[] data = cubes.get(i).getStorage().getBlockLight().getData();
                in.readBytes(data);
            }
        }

        // 4. sky light
        for (int i = 0; i < cubes.size(); i++) {
            if (hasStorage[i] && cubes.get(i).getWorld().provider.hasSkyLight()) {
                //noinspection ConstantConditions
                byte[] data = cubes.get(i).getStorage().getSkyLight().getData();
                in.readBytes(data);
            }
        }

        int[] oldHeights = new int[Cube.SIZE * Cube.SIZE];
        // 5. heightmaps and after all that - update ref counts
        for (int i = 0; i < cubes.size(); i++) {
            if (!isEmpty[i]) {
                Cube cube = cubes.get(i);
                ILightingManager lm = ((ICubicWorldInternal) cube.getWorld()).getLightingManager();
                IColumnInternal column = cube.getColumn();
                ClientHeightMap coi = (ClientHeightMap) column.getOpacityIndex();
                for (int dx = 0; dx < Cube.SIZE; dx++) {
                    for (int dz = 0; dz < Cube.SIZE; dz++) {
                        oldHeights[AddressTools.getLocalAddress(dx, dz)] = coi.getTopBlockY(dx, dz);
                    }
                }
                column.loadClientHeightmapData(in);
                for (int dx = 0; dx < Cube.SIZE; dx++) {
                    for (int dz = 0; dz < Cube.SIZE; dz++) {
                        int oldY = oldHeights[AddressTools.getLocalAddress(dx, dz)];
                        int newY = coi.getTopBlockY(dx, dz);
                        if (oldY != newY) {
                            lm.updateLightBetween(cube.getColumn(), dx, oldY, newY, dz);
                        }
                    }
                }
                //noinspection ConstantConditions
                cube.getStorage().recalculateRefCounts();
            }
        }
        
        // 6. biomes
        for (int i = 0; i < cubes.size(); i++) {
            if (!hasCustomBiomeMap[i])
                continue;
            Cube cube = cubes.get(i);
            int[] blockBiomeArray = new int[Coords.BIOMES_PER_CUBE];
            for (int j = 0; j < 64; j++) 
                blockBiomeArray[j] = in.readInt();
            ((INewCube)cube).setBiomeArray(blockBiomeArray);
        }
    }

    @Overwrite
    static int getEncodedSize(Chunk column) {
        return (((INewChunk) column).getIntBiomeArray().length * Integer.BYTES) + (Cube.SIZE * Cube.SIZE * Integer.BYTES);
    }

    @Overwrite
    static int getEncodedSize(Collection<Cube> cubes) {
        int size = 0;

        // 1. isEmpty, hasStorage and hasBiomeArray flags packed in one byte
        size += cubes.size();

        // 2. block IDs and metadata
        for (Cube cube : cubes) {
            if (!cube.isEmpty()) {
                //noinspection ConstantConditions
                size += cube.getStorage().getData().getSerializedSize();
            }
            if (cube.getStorage() != null) {
                size += cube.getStorage().getBlockLight().getData().length;
                if (cube.getWorld().provider.hasSkyLight()) {
                    size += cube.getStorage().getSkyLight().getData().length;
                }
            }
        }

        // heightmaps
        size += Cube.SIZE * Cube.SIZE * Integer.BYTES * cubes.size();
        // biomes
        for (Cube cube : cubes) {
            int[] biomeArray = ((INewCube)cube).getBiomeArray();
            if (biomeArray == null)
                continue;
            size += Integer.BYTES * biomeArray.length;
        }
        return size;
    }
}
