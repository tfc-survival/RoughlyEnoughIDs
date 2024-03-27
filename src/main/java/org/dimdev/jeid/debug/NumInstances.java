package org.dimdev.jeid.debug;

import org.dimdev.jeid.config.ConfigHandler;

/**
 * Enum to specify number of instances of each type to register.
 * <p>
 * Edit values here to increase/decrease number of instances registered.
 */
public enum NumInstances {
    BIOME(ConfigHandler.DEBUG.reidDebugBiomesAmt),
    BLOCK(ConfigHandler.DEBUG.reidDebugBlocksAmt),
    ENCHANT(ConfigHandler.DEBUG.reidDebugEnchantsAmt),
    ITEM(ConfigHandler.DEBUG.reidDebugItemsAmt),
    POTION(ConfigHandler.DEBUG.reidDebugPotionsAmt);

    public final int value;

    NumInstances(int val) {
        this.value = val;
    }
}
