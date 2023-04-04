package org.dimdev.jeid;

import net.minecraft.world.chunk.NibbleArray;

public interface INewBlockStateContainer {
    void setTemporaryPalette(int[] temporaryPalette);
    int[] getTemporaryPalette();
    void setLegacyAdd2(NibbleArray add2);
}
