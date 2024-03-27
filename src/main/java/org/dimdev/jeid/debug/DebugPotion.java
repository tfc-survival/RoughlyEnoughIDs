package org.dimdev.jeid.debug;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.config.ConfigHandler;

import java.util.Random;

public class DebugPotion extends DebugBase<Potion> {
    // For easy registration of PotionType
    private final IForgeRegistry<PotionType> typeRegistry;

    public DebugPotion(int numInstances, IForgeRegistry<Potion> registry) {
        super(numInstances, registry);
        typeRegistry = GameRegistry.findRegistry(PotionType.class);
    }

    @Override
    public void makeInstance(int id) {
        Potion instance = new PotionTest(id)
                .setRegistryName(new ResourceLocation(JEID.MODID, "potion_" + id));
        PotionType instanceType = new PotionType(new PotionEffect(instance, 2000, 0, false, true))
                .setRegistryName(new ResourceLocation(JEID.MODID, "potiontype_" + id));
        registry.register(instance);
        typeRegistry.register(instanceType);
    }

    @Override
    public boolean shouldDebug() {
        return ConfigHandler.DEBUG.reidDebugPotionsToggle;
    }

    private static class PotionTest extends Potion {

        private static final Random r = new Random();
        private final String nm;

        protected PotionTest(int id) {
            super(false, 0xFFFFFF & r.nextInt(Integer.MAX_VALUE));
            nm = "Test Potion #" + id;
        }

        @Override
        public String getName() {
            return nm;
        }
    }
}
