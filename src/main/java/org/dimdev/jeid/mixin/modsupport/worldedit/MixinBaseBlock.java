package org.dimdev.jeid.mixin.modsupport.worldedit;

import com.sk89q.worldedit.blocks.BaseBlock;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BaseBlock.class, remap = false)
public class MixinBaseBlock {
    @Unique
    private int reid$intId = 0;

    @Inject(method = "getId", at = @At(value = "RETURN"), cancellable = true)
    private void reid$getIntId(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(reid$intId);
    }

    @ModifyConstant(method = "internalSetId", constant = @Constant(intValue = 4095))
    private int reid$getMaxBlockId(int oldValue) {
        return Integer.MAX_VALUE - 1;
    }

    @Dynamic("Set block id as int and set short id 0")
    @ModifyVariable(method = "internalSetId", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.PUTFIELD), argsOnly = true)
    private int reid$setIntId(int id) {
        reid$intId = id;
        return 0;
    }
}
