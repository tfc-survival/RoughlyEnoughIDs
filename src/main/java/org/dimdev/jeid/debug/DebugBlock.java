package org.dimdev.jeid.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.config.ConfigHandler;

public class DebugBlock extends DebugBase<Block> {
    // For easy registration of ItemBlock
    private final IForgeRegistry<Item> itemRegistry;

    public DebugBlock(int numInstances, IForgeRegistry<Block> registry) {
        super(numInstances, registry);
        itemRegistry = GameRegistry.findRegistry(Item.class);
    }

    @Override
    public void makeInstance(int id) {
        ResourceLocation loc = new ResourceLocation(JEID.MODID, "block_" + id);
        Block instance = new Block(Material.GROUND)
                .setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
                .setRegistryName(loc);
        registry.register(instance);
        itemRegistry.register(new ItemBlock(instance).setRegistryName(loc));
    }

    @Override
    public boolean shouldDebug() {
        return ConfigHandler.DEBUG.reidDebugBlocksToggle;
    }
}
