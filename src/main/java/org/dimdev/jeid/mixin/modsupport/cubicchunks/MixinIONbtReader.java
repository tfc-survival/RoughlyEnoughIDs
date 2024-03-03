package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtReader;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.World;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.*;

@Pseudo
@Mixin(IONbtReader.class)
public class MixinIONbtReader {
    @Overwrite
    private static void readBlocks(NBTTagCompound nbt, World world, Cube cube) {
        boolean isEmpty = !nbt.hasKey("Sections");// is this an empty cube?
        if (!isEmpty) {
            NBTTagList sectionList = nbt.getTagList("Sections", 10);
            nbt = sectionList.getCompoundTagAt(0);

            ExtendedBlockStorage ebs = new ExtendedBlockStorage(Coords.cubeToMinBlock(cube.getY()), cube.getWorld().provider.hasSkyLight());

            int[] palette = nbt.hasKey("Palette", 11) ? nbt.getIntArray("Palette") : null;
            //Utils.LOGGER.info("cube at {}, {} palette size {}", cube.getCoords().getX(), cube.getCoords().getZ(), palette.length);
            ((INewBlockStateContainer) ebs.getData()).setTemporaryPalette(palette);

            byte[] abyte = nbt.getByteArray("Blocks");
            NibbleArray data = new NibbleArray(nbt.getByteArray("Data"));
            NibbleArray add = nbt.hasKey("Add", 7) ? new NibbleArray(nbt.getByteArray("Add")) : null;

            ebs.getData().setDataFromNBT(abyte, data, add);

            ebs.setBlockLight(new NibbleArray(nbt.getByteArray("BlockLight")));
            if (world.provider.hasSkyLight()) {
                ebs.setSkyLight(new NibbleArray(nbt.getByteArray("SkyLight")));
            }

            ebs.recalculateRefCounts();
            cube.setStorage(ebs);
        }
    }

    @Overwrite
    private static void readBiomes(NBTTagCompound nbt, Chunk column) {// column biomes
        System.arraycopy(nbt.getIntArray("Biomes"), 0, ((INewChunk)column).getIntBiomeArray(), 0, Cube.SIZE * Cube.SIZE);
    }

    @Overwrite
    private static void readBiomes(Cube cube, NBTTagCompound nbt) {// cube biomes
        if (nbt.hasKey("Biomes3D")) {
            ((INewCube) cube).setBiomeArray(nbt.getIntArray("Biomes3D"));
        }
        if (nbt.hasKey("Biomes"))
        {
            ((INewCube) cube).setBiomeArray(convertFromOldCubeBiomes(nbt.getIntArray("Biomes")));
        }
    }

    private static int[] convertFromOldCubeBiomes(int[] biomes) {
        int[] newBiomes = new int[64];

        for(int x = 0; x < 4; ++x) {
            for(int y = 0; y < 4; ++y) {
                for(int z = 0; z < 4; ++z) {
                    newBiomes[AddressTools.getBiomeAddress3d(x, y, z)] = biomes[getOldBiomeAddress(x << 1 | y & 1, z << 1 | y >> 1 & 1)];
                }
            }
        }

        return newBiomes;
    }

    @Shadow
    public static int getOldBiomeAddress(int biomeX, int biomeZ) { return biomeX << 3 | biomeZ; }
}
