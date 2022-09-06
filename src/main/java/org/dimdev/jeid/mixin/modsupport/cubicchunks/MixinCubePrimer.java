package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

@Pseudo
@Mixin(CubePrimer.class)
@SuppressWarnings("deprecation")
public class MixinCubePrimer {
    @Shadow private static int getBlockIndex(int x, int y, int z) { return 0; }
    @Shadow @Final private static IBlockState DEFAULT_STATE;
    private int[] intData = new int[65536];
    
    /**
     * Get the block state at the given location
     *
     * @param x cube local x
     * @param y cube local y
     * @param z cube local z
     * @return the block state
     */
    @Overwrite(remap = false)
    public IBlockState getBlockState(int x, int y, int z) {
        IBlockState state = Block.BLOCK_STATE_IDS.getByValue(intData[getBlockIndex(x, y, z)]);
        return state == null ? DEFAULT_STATE : state;
    }

    /**
     * Set the block state at the given location
     *
     * @param x     cube local x
     * @param y     cube local y
     * @param z     cube local z
     * @param state the block state
     */
    @Overwrite(remap = false)
    public void setBlockState(int x, int y, int z, @Nonnull IBlockState state) {
        intData[getBlockIndex(x, y, z)] = Block.BLOCK_STATE_IDS.get(state);
    }
}
