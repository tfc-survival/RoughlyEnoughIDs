package org.dimdev.jeid.debug;

/**
 * Enum to specify number of instances of each type to register.
 * <p>
 * Edit values here to increase/decrease number of instances registered.
 */
public enum NumInstances {
    BLOCK(5000),
    ITEM(40000),
    BIOME(300),
    POTION(300),
    ENCHANT(Short.MAX_VALUE);

    public final int value;

    NumInstances(int val) {
        this.value = val;
    }
}
