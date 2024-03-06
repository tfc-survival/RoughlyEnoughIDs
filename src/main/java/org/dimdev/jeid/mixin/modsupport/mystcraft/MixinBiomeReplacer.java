package org.dimdev.jeid.mixin.modsupport.mystcraft;

import com.llamalad7.mixinextras.sugar.Local;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolFloatingIslands;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SymbolFloatingIslands.BiomeReplacer.class, remap = false)
public class MixinBiomeReplacer {
    @Shadow private Biome biome;
    @Inject(method = "finalizeChunk", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I", remap = true))
    private void reid$toIntBiomeArray(Chunk chunk, int chunkX, int chunkZ, CallbackInfo ci, @Local(ordinal = 2) int coords) {
        ((INewChunk) chunk).getIntBiomeArray()[coords] = Biome.getIdForBiome(biome);
    }
}
