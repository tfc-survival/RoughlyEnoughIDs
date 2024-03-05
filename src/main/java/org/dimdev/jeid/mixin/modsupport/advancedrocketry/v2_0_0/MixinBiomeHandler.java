package org.dimdev.jeid.mixin.modsupport.advancedrocketry.v2_0_0;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.util.BiomeHandler;

/**
 * Mixin for Advanced Rocketry 2.0.0+
 * Biome changer remote is broken in this version, so this doesn't do much.
 */
@Mixin(value = BiomeHandler.class, remap = false)
public class MixinBiomeHandler {
    @Dynamic("Use int biome array for AR 2.0.0")
    @Inject(method = "changeBiome(Lnet/minecraft/world/World;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lzmaster587/libVulpes/network/PacketHandler;sendToNearby(Lzmaster587/libVulpes/network/BasePacket;ILnet/minecraft/util/math/BlockPos;D)V"), require = 0)
    private static void reid$toIntBiomeArray2_0_0(World world, Biome biomeId, BlockPos pos, CallbackInfo ci, @Local Chunk chunk) {
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = Biome.getIdForBiome(biomeId);
        chunk.markDirty();
    }
}
