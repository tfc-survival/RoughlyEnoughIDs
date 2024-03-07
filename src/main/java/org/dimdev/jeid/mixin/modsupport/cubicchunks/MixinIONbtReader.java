package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtReader;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IONbtReader.class, remap = false)
public class MixinIONbtReader {
    @Inject(method = "readBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getByteArray(Ljava/lang/String;)[B", ordinal = 0, remap = true))
    private static void reid$setTempPalette(NBTTagCompound nbt, World world, Cube cube, CallbackInfo ci, @Local ExtendedBlockStorage ebs) {
        int[] palette = nbt.hasKey("Palette", 11) ? nbt.getIntArray("Palette") : null;
        //JEID.LOGGER.info("cube at {}, {} palette size {}", cube.getCoords().getX(), cube.getCoords().getZ(), palette.length);
        ((INewBlockStateContainer) ebs.getData()).setTemporaryPalette(palette);
    }

    @ModifyConstant(method = "readBlocks", constant = @Constant(intValue = 4096))
    private static int reid$setBlockStateContainerData(int oldValue, @Local ExtendedBlockStorage ebs, @Local byte[] abyte,
                                                       @Local(ordinal = 0) NibbleArray data, @Local(ordinal = 1) NibbleArray add) {
        ebs.getData().setDataFromNBT(abyte, data, add);
        return 0;
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    private static void readBiomes(NBTTagCompound nbt, Chunk column) {// column biomes
        System.arraycopy(nbt.getIntArray("Biomes"), 0, ((INewChunk) column).getIntBiomeArray(), 0, Cube.SIZE * Cube.SIZE);
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    private static void readBiomes(Cube cube, NBTTagCompound nbt) {// cube biomes
        if (nbt.hasKey("Biomes3D")) {
            ((INewCube) cube).setBiomeArray(nbt.getIntArray("Biomes3D"));
        }
        if (nbt.hasKey("Biomes")) {
            ((INewCube) cube).setBiomeArray(convertFromOldCubeBiomes(nbt.getIntArray("Biomes")));
        }
    }

    @Unique
    private static int[] convertFromOldCubeBiomes(int[] biomes) {
        int[] newBiomes = new int[64];

        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = 0; z < 4; ++z) {
                    newBiomes[AddressTools.getBiomeAddress3d(x, y, z)] = biomes[getOldBiomeAddress(x << 1 | y & 1, z << 1 | y >> 1 & 1)];
                }
            }
        }

        return newBiomes;
    }

    @Shadow
    public static int getOldBiomeAddress(int biomeX, int biomeZ) {
        return 0;
    }
}
