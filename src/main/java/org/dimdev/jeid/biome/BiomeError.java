package org.dimdev.jeid.biome;

import net.minecraft.world.biome.BiomeVoid;
import org.dimdev.jeid.JEID;

public class BiomeError extends BiomeVoid {
    private static final BiomeError INSTANCE = new BiomeError("error_biome", "A mod doesn't support extended biome IDs -- report to JEID");

    private BiomeError(String idName, String propName) {
        super(new BiomeProperties(propName));
        this.setRegistryName(JEID.MODID, idName);
    }

    public static BiomeError getInstance() {
        return BiomeError.INSTANCE;
    }
}
