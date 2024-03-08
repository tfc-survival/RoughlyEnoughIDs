package org.dimdev.jeid.mixin.modsupport.abyssalcraft;

import com.llamalad7.mixinextras.sugar.Local;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BiomeUtil.class, remap = false)
public class MixinBiomeUtil {
    @Inject(method = "updateBiome(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;IZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setModified(Z)V", remap = true))
    private static void reid$toIntBiomeArray(World worldIn, BlockPos pos, int b, boolean batched, CallbackInfo ci, @Local Chunk chunk) {
        // Method calls setModified(true), same as markDirty()
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = b;
        // Method sends packet
    }
}
