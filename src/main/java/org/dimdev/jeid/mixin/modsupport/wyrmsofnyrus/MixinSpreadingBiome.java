package org.dimdev.jeid.mixin.modsupport.wyrmsofnyrus;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.vetpetmon.wyrmsofnyrus.world.biome.BiomeRegistry;
import com.vetpetmon.wyrmsofnyrus.world.biome.SpreadingBiome;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SpreadingBiome.class, remap = false)
public class MixinSpreadingBiome {
    // I would natively implement this considering it doesn't even need anything fancy,
    // but I must agree that making a one-off patch is much more performant than making the game
    // check for REID, though I am concerned if people are using JEID and don't know about REID...
    //
    // In 0.6 or a later 0.5.x release I'll probably figure out a way to make a native patch.
    //
    // WoN's biome alteration code is similar to Thaumcraft's and SRP's biome alteration,
    // so these patches are based on those mods' patches.
    // I keep calling Mixins patches. I see no difference.
    // Can you believe this is the first Mixin I made that actually works? -Modrome, official WoN dev
    /**
     * @author Modrome, jchung01
     * @reason Patches Wyrms of Nyrus's Biome Spread compatible for 0.5.x
     */
    @Inject(method = "setBiome(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;", remap = true), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void reid$toIntBiomeArray(World w, BlockPos pos, Biome biome, CallbackInfo ci,
                                             int convX, int convZ) {
        Chunk chunk = w.getChunk(pos);
        chunk.markDirty();
        ((INewChunk) chunk).getIntBiomeArray()[convZ << 4 | convX] = Biome.getIdForBiome(biome);
        ci.cancel();
    }

    /**
     * @reason Overload for WoN 0.5.x
     */
    @Inject(method = "setBiome(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$creeplandsToIntBiomeArray(World w, BlockPos pos, CallbackInfo ci) {
        SpreadingBiome.setBiome(w, pos, BiomeRegistry.CREEPLANDS);
        ci.cancel();
    }

    // TODO: Add mixin for 0.6 if necessary (annotate with @Group and @Dynamic, see AR MixinBiomeHandler for example)
}
