package org.dimdev.jeid.mixin.modsupport.advancedrocketry.v1_7_0;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.util.BiomeHandler;

/**
 * Mixin for Advanced Rocketry 1.7.0
 */
@Mixin(value = BiomeHandler.class, remap = false)
public class MixinBiomeHandler {
    @Dynamic("Only present with AR 1.7.0")
    @Shadow
    public static void changeBiome(World world, int biomeId, BlockPos pos) {
    }

    @Dynamic("Use int biome array for AR 1.7.0")
    @Inject(method = "changeBiome(Lnet/minecraft/world/World;ILnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lzmaster587/libVulpes/network/PacketHandler;sendToNearby(Lzmaster587/libVulpes/network/BasePacket;ILnet/minecraft/util/math/BlockPos;D)V"), require = 0)
    private static void reid$toIntBiomeArray1_7_0(World world, int biomeId, BlockPos pos, CallbackInfo ci, @Local Chunk chunk) {
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = biomeId;
        chunk.markDirty();
    }

    @Dynamic("Overload for AR 1.7.0")
    @Inject(method = "changeBiome(Lnet/minecraft/world/World;ILnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "HEAD"), cancellable = true, require = 0)
    private static void reid$toIntBiomeArray1_7_0Chunk(World world, int biomeId, Chunk chunk, BlockPos pos, CallbackInfo ci) {
        changeBiome(world, biomeId, pos);
        ci.cancel();
    }
}
