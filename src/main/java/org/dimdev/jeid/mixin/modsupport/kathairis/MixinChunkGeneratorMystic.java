package org.dimdev.jeid.mixin.modsupport.kathairis;

import net.minecraft.world.biome.Biome;

import mod.krevik.world.dimension.ChunkGeneratorMystic;
import org.dimdev.jeid.ducks.ICustomBiomesForGeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ChunkGeneratorMystic.class, remap = false)
public class MixinChunkGeneratorMystic implements ICustomBiomesForGeneration {
    @Shadow
    private Biome[] biomesForGeneration;

    @Override
    public Biome[] getBiomesForGeneration() {
        return biomesForGeneration;
    }
}
