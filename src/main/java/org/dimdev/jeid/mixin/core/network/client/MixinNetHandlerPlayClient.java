package org.dimdev.jeid.mixin.core.network.client;

import net.minecraft.client.network.NetHandlerPlayClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    /**
     * @reason Account for JEID blockstate format (32 bits, not 16).
     */
    @ModifyConstant(method = "handleSpawnObject",
            slice = @Slice(
                    id = "fallingBlock",
                    from = @At(value = "NEW", target = "(Lnet/minecraft/world/World;DDDLnet/minecraft/block/state/IBlockState;)Lnet/minecraft/entity/item/EntityFallingBlock;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getStateById(I)Lnet/minecraft/block/state/IBlockState;")
            ), constant = @Constant(intValue = 0xFFFF, slice = "fallingBlock"))
    private int reid$getJEIDBlockstate(int constant) {
        return 0xFFFFFFFF;
    }
}
