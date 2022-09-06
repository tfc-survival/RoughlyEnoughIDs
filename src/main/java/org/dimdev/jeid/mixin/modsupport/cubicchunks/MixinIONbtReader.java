package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtReader;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.World;
import org.dimdev.jeid.INewBlockStateContainer;
import org.dimdev.jeid.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

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

}
