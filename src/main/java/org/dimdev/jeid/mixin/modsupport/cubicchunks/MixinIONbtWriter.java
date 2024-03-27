package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(targets = "io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtWriter", remap = false)
public class MixinIONbtWriter {
    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    private static void writeBiomes(Chunk column, NBTTagCompound nbt) {// column biomes
        nbt.setIntArray("Biomes", ((INewChunk) column).getIntBiomeArray());
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    private static void writeBiomes(Cube cube, NBTTagCompound nbt) {// cube biomes
        int[] biomes = ((INewCube) cube).getBiomeArray();
        if (biomes != null)
            nbt.setIntArray("Biomes", biomes);
    }

    @ModifyConstant(method = "writeBlocks",
            slice = @Slice(
                    id = "loopCondition",
                    from = @At(value = "NEW", target = "()Lnet/minecraft/world/chunk/NibbleArray;", remap = true)
            ), constant = @Constant(intValue = 4096, slice = "loopCondition"))
    private static int reid$setNBTPalette(int oldValue, @Local ExtendedBlockStorage ebs, @Local(ordinal = 1) NBTTagCompound section,
                                          @Local byte[] abyte, @Local(ordinal = 0) NibbleArray data,
                                          @Local(ordinal = 1) LocalRef<NibbleArray> add) {
        add.set(ebs.getData().getDataForNBT(abyte, data));

        int[] palette = ((INewBlockStateContainer) ebs.getData()).getTemporaryPalette();
        //JEID.LOGGER.info("cube at {}, {} palette size {}", cube.getCoords().getX(), cube.getCoords().getZ(), palette.length);
        if (palette != null) section.setIntArray("Palette", palette);
        return 0;
    }
}
