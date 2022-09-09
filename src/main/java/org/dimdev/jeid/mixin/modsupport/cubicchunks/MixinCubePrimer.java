package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Pseudo
@Mixin(CubePrimer.class)
@SuppressWarnings("deprecation")
public class MixinCubePrimer {
    @Shadow @Final private char[] data;
    @Shadow @Final private static IBlockState DEFAULT_STATE;
    @Shadow private static int getBlockIndex(int x, int y, int z) { return 0; }

    private char[] extIntData = null;

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
        int idx = getBlockIndex(x, y, z);
        int block = this.data[idx];
        if (extIntData != null) {
            block |= extIntData[idx] << 16;
        }
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(block);
        return iblockstate == null ? DEFAULT_STATE : iblockstate;
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
        int value = Block.BLOCK_STATE_IDS.get(state);
        char lsb = (char) value;
        int idx = getBlockIndex(x, y, z);
        this.data[idx] = lsb;
        if (value > 0xFFFF) {
            if (extIntData == null) {
                extIntData = new char[4096];
            }
            extIntData[idx] = (char) (value >>> 16);
        }
    }
}
