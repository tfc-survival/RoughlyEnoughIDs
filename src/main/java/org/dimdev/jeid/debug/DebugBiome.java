package org.dimdev.jeid.debug;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;

public class DebugBiome extends DebugBase<Biome> {
    public DebugBiome(int numInstances, IForgeRegistry<Biome> registry) {
        super(numInstances, registry);
    }

    @Override
    public void makeInstance(int id) {
        Biome instance = new Biome(new Biome.BiomeProperties("Biome " + id)) {
        }
                .setRegistryName(new ResourceLocation(JEID.MODID, "biome_" + id));
        registry.register(instance);
    }

    @Override
    public boolean shouldDebug() {
        return false;
    }
}
