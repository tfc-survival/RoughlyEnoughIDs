package org.dimdev.jeid.mixin.modsupport.compactmachines;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dave.compactmachines3.utility.ChunkUtils;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkUtils.class, remap = false)
public class MixinChunkUtils {
    @Inject(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", ordinal = 0, remap = true))
    private static void reid$setNBTPalette(CallbackInfoReturnable<NBTTagCompound> cir, @Local(ordinal = 1) NBTTagCompound nbtTagCompound, @Local ExtendedBlockStorage extendedBlockStorage) {
        int[] palette = ((INewBlockStateContainer) extendedBlockStorage.getData()).getTemporaryPalette();
        nbtTagCompound.setIntArray("Palette", palette);
    }

    @Redirect(method = "writeChunkToNBT",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", ordinal = 0, remap = true),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setHasEntities(Z)V", remap = true)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", remap = true)
    )
    private static void reid$setNBTBiomeArray(NBTTagCompound instance, String key, byte[] value, Chunk chunkIn) {
        if (!key.equals("Biomes")) {
            throw new AssertionError(JEID.MODID + " :: NBTTagCompound#setByteArray key of writeChunkToNBT isn't \"Biomes\"");
        }
        instance.setIntArray(key, ((INewChunk) chunkIn).getIntBiomeArray());
    }

    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getByteArray(Ljava/lang/String;)[B", ordinal = 0, remap = true))
    private static void reid$setTempPalette(CallbackInfoReturnable<Chunk> cir, @Local(ordinal = 1) NBTTagCompound nbtTagCompound, @Local ExtendedBlockStorage extendedBlockStorage) {
        int[] palette = nbtTagCompound.hasKey("Palette", 11) ? nbtTagCompound.getIntArray("Palette") : null;
        ((INewBlockStateContainer) extendedBlockStorage.getData()).setTemporaryPalette(palette);
    }

    @Redirect(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V", remap = true))
    private static void reid$setBiomeArray(Chunk instance, byte[] original, World worldIn, NBTTagCompound compound) {
        ((INewChunk) instance).setIntBiomeArray(compound.getIntArray("Biomes"));
    }
}
