package org.dimdev.jeid.debug;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;

public class DebugEnchant extends DebugBase<Enchantment> {
    public DebugEnchant(int numInstances, IForgeRegistry<Enchantment> registry) {
        super(numInstances, registry);
    }

    @Override
    public void makeInstance(int id) {
        Enchantment instance = new EnchantTest(id)
                .setRegistryName(new ResourceLocation(JEID.MODID, "enchant_" + id));
        registry.register(instance);
    }

    @Override
    public boolean shouldDebug() {
        return false;
    }

    private static class EnchantTest extends Enchantment {

        public EnchantTest(int i) {
            super(Rarity.COMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[EntityEquipmentSlot.CHEST.getIndex()]);
            this.setName("Test Enchantment #" + i);
        }

    }
}
