package org.dimdev.jeid.debug;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegistryDebug {
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IDebugClass debugBlocks = new DebugBlock(NumInstances.BLOCK.value, event.getRegistry());
        if (debugBlocks.shouldDebug()) makeAllInstances(debugBlocks);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IDebugClass debugItems = new DebugItem(NumInstances.ITEM.value, event.getRegistry());
        if (debugItems.shouldDebug()) makeAllInstances(debugItems);
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        IDebugClass debugBiomes = new DebugBiome(NumInstances.BIOME.value, event.getRegistry());
        if (debugBiomes.shouldDebug()) makeAllInstances(debugBiomes);
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        IDebugClass debugPotions = new DebugPotion(NumInstances.POTION.value, event.getRegistry());
        if (debugPotions.shouldDebug()) makeAllInstances(debugPotions);
    }

    @SubscribeEvent
    public void registerEnchants(RegistryEvent.Register<Enchantment> event) {
        IDebugClass debugEnchants = new DebugEnchant(NumInstances.ENCHANT.value, event.getRegistry());
        if (debugEnchants.shouldDebug()) makeAllInstances(debugEnchants);
    }

    private void makeAllInstances(IDebugClass debugClass) {
        for (int i = 0; i < debugClass.getNumInstances(); ++i) {
            debugClass.makeInstance(i);
        }
    }
}
