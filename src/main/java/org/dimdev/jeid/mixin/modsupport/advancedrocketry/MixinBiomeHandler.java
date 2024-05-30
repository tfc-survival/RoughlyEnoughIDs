package org.dimdev.jeid.mixin.modsupport.advancedrocketry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import zmaster587.advancedRocketry.util.BiomeHandler;

@SuppressWarnings("target")
@Mixin(value = BiomeHandler.class, remap = false)
public class MixinBiomeHandler {
    // Do not use @Local sugar for these injectors, it doesn't handle invalid targets well!

    @Dynamic("Use int biome array for AR 1.7.0")
    @Group(name = "versionAgnosticAR", min = 1, max = 2)
    @Inject(method = "changeBiome(Lnet/minecraft/world/World;ILnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lzmaster587/libVulpes/network/PacketHandler;sendToNearby(Lzmaster587/libVulpes/network/BasePacket;ILnet/minecraft/util/math/BlockPos;D)V"), locals = LocalCapture.CAPTURE_FAILHARD, require = 0)
    private static void reid$toIntBiomeArray1_7_0(World world, int biomeId, BlockPos pos, CallbackInfo ci, Chunk chunk) {
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = biomeId;
        chunk.markDirty();
        // Method sends packet
    }

    @Dynamic("Overload for AR 1.7.0")
    @Group(name = "versionAgnosticAR", min = 1, max = 2)
    @Inject(method = "changeBiome(Lnet/minecraft/world/World;ILnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "HEAD"), cancellable = true, require = 0)
    private static void reid$toIntBiomeArray1_7_0Chunk(World world, int biomeId, Chunk chunk, BlockPos pos, CallbackInfo ci) {
        BiomeHandler.changeBiome(world, biomeId, pos);
        ci.cancel();
    }

    @Dynamic("Use int biome array for AR 2.0.0")
    @Group(name = "versionAgnosticAR", min = 1, max = 2)
    @Inject(method = "changeBiome(Lnet/minecraft/world/World;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lzmaster587/libVulpes/network/PacketHandler;sendToNearby(Lzmaster587/libVulpes/network/BasePacket;ILnet/minecraft/util/math/BlockPos;D)V"), locals = LocalCapture.CAPTURE_FAILHARD, require = 0)
    private static void reid$toIntBiomeArray2_0_0(World world, Biome biomeId, BlockPos pos, CallbackInfo ci, Chunk chunk) {
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = Biome.getIdForBiome(biomeId);
        chunk.markDirty();
        // Method sends packet
    }
}
