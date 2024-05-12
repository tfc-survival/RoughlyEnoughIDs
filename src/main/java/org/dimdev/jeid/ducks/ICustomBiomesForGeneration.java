package org.dimdev.jeid.ducks;

import net.minecraft.world.biome.Biome;

/**
 * Duck interface for mixins into certain mods with custom chunk generators.
 * If your mod uses a custom chunk generator and modifies the biome array returned by:
 * <p>{@code this.world.getBiomeProvider().getBiomes(...)},</p>
 * implement this in your IChunkGenerator.
 */
public interface ICustomBiomesForGeneration {
    /**
     * Returns the modified biome array (usually called {@code biomesForGeneration}).
     *
     * @return the modified biome array
     */
    Biome[] getBiomesForGeneration();
}
