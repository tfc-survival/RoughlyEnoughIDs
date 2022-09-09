package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.INewBlockStateContainer;
import org.dimdev.jeid.INewChunk;
import org.dimdev.jeid.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtWriter")
public class MixinIONbtWriter {

    @Overwrite
    private static void writeBiomes(Chunk column, NBTTagCompound nbt) {// column biomes
        nbt.setIntArray("Biomes", ((INewChunk)column).getIntBiomeArray());
    }

    @Overwrite
    private static void writeBiomes(Cube cube, NBTTagCompound nbt) {// cube biomes
        INewCube newCube = (INewCube) cube;
        int[] biomes = newCube.getIntBiomeArray();
        if (biomes != null)
            nbt.setIntArray("Biomes", biomes);
    }

    @Overwrite
    private static void writeBlocks(Cube cube, NBTTagCompound cubeNbt) {
        ExtendedBlockStorage ebs = cube.getStorage();
        if (ebs == null) {
            return; // no data to save anyway
        }
        NBTTagList sectionList = new NBTTagList();
        NBTTagCompound section = new NBTTagCompound();
        sectionList.appendTag(section);
        cubeNbt.setTag("Sections", sectionList);
        byte[] abyte = new byte[Cube.SIZE * Cube.SIZE * Cube.SIZE];
        NibbleArray data = new NibbleArray();
        NibbleArray add = ebs.getData().getDataForNBT(abyte, data);

        int[] palette = ((INewBlockStateContainer) ebs.getData()).getTemporaryPalette();
        //Utils.LOGGER.info("cube at {}, {} palette size {}", cube.getCoords().getX(), cube.getCoords().getZ(), palette.length);
        if (palette != null) section.setIntArray("Palette", palette);

        section.setByteArray("Blocks", abyte);
        section.setByteArray("Data", data.getData());

        if (add != null) {
            section.setByteArray("Add", add.getData());
        }

        section.setByteArray("BlockLight", ebs.getBlockLight().getData());

        if (cube.getWorld().provider.hasSkyLight()) {
            section.setByteArray("SkyLight", ebs.getSkyLight().getData());
        }
    }

}
