package org.dimdev.jeid.mixin.modsupport.extrautils2;

import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils2.dimensions.workhousedim.WorldProviderSpecialDim;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = WorldProviderSpecialDim.class, remap = false)
public class MixinWorldProviderSpecialDim {
    @Shadow
    public static Biome biome;

    @Inject(method = "generate", at = @At(target = "Lnet/minecraft/world/chunk/Chunk;setTerrainPopulated(Z)V", value = "INVOKE", ordinal = 0, remap = true))
    private static void reid$setBiomeArray(CallbackInfo ci, @Local Chunk chunk) {
        Arrays.fill(((INewChunk) chunk).getIntBiomeArray(), Biome.getIdForBiome(biome));
    }
}
