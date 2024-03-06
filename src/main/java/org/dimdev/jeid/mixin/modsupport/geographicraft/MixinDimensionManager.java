package org.dimdev.jeid.mixin.modsupport.geographicraft;

import climateControl.DimensionManager;
import com.rwtema.extrautils2.biome.BiomeManip;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DimensionManager.class, remap = false)
public class MixinDimensionManager {
    /**
     * @reason Support int biome ids and rewrite {@link BiomeManip#setBiome} because of var types
     */
    @Inject(method = "hasOnlySea", at = @At(value = "HEAD"), cancellable = true)
    private void reid$rewriteHasOnlySea(Chunk tested, CallbackInfoReturnable<Boolean> cir) {
        for (int biome : ((INewChunk) tested).getIntBiomeArray()) {
            if (biome != 0 && biome != Biome.getIdForBiome(Biomes.DEEP_OCEAN)) {
                cir.setReturnValue(false);
            }
        }
        cir.setReturnValue(true);
    }
}
