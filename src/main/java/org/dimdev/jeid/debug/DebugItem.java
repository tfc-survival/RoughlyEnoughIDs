package org.dimdev.jeid.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.config.ConfigHandler;

public class DebugItem extends DebugBase<Item> {
    public DebugItem(int numInstances, IForgeRegistry<Item> registry) {
        super(numInstances, registry);
    }

    @Override
    public void makeInstance(int id) {
        Item instance = new Item()
                .setCreativeTab(CreativeTabs.FOOD)
                .setRegistryName(new ResourceLocation(JEID.MODID, "item_" + id));
        registry.register(instance);
    }

    @Override
    public boolean shouldDebug() {
        return ConfigHandler.DEBUG.reidDebugItemsToggle;
    }
}
