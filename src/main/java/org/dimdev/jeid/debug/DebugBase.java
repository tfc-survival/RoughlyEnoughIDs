package org.dimdev.jeid.debug;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DebugBase<T extends IForgeRegistryEntry<T>> implements IDebugClass {
    private final int numInstances;
    protected IForgeRegistry<T> registry;

    public DebugBase(int numInstances, IForgeRegistry<T> registry) {
        this.numInstances = numInstances;
        this.registry = registry;
    }

    @Override
    public int getNumInstances() {
        return numInstances;
    }
}
