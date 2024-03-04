package org.dimdev.jeid.mixin.core.world;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkPrimer.class)
public class MixinChunkPrimer {
    @Unique
    private int[] intData = new int[65536];

    @Shadow
    private static int getBlockIndex(int x, int y, int z) {
        return 0;
    }

    @ModifyArg(method = "getBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ObjectIntIdentityMap;getByValue(I)Ljava/lang/Object;"))
    private int reid$getIntDataForBlockState(int original, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        return intData[getBlockIndex(x, y, z)];
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "setBlockState", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/ChunkPrimer;data:[C"), cancellable = true)
    private void reid$setIntBlockState(int x, int y, int z, IBlockState state, CallbackInfo ci) {
        intData[getBlockIndex(x, y, z)] = Block.BLOCK_STATE_IDS.get(state);
        ci.cancel();
    }

    @ModifyArg(method = "findGroundBlockIdx", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ObjectIntIdentityMap;getByValue(I)Ljava/lang/Object;"))
    private int reid$getIntDataForGroundBlock(int original, @Local(ordinal = 2) int i, @Local(ordinal = 3) int j) {
        return intData[i + j];
    }
}
