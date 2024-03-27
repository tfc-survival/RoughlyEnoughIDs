package org.dimdev.jeid.mixin.core.world.client;

import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldSummary.class)
public class MixinWorldSummary {
    @Shadow
    @Final
    private int versionId;

    // @ModifyReturnValue doesn't seem to work here
    @Inject(method = "askToOpenWorld", at = @At(value = "RETURN"), cancellable = true)
    private void reid$checkVersionId(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && (versionId != Integer.MAX_VALUE / 2));
    }
}
