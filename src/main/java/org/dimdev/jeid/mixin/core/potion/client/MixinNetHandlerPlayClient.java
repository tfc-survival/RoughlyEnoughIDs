package org.dimdev.jeid.mixin.core.potion.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketEntityEffect;
import org.dimdev.jeid.ducks.IStoredEffectInt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin to read stored effect id as int. Converted from JEIDTransformer#transformNetHandlerPlayClient
 *
 * @see org.dimdev.jeid.core.JEIDTransformer
 */
@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Final
    @ModifyArg(method = "handleEntityEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potion;getPotionById(I)Lnet/minecraft/potion/Potion;"), index = 0)
    private int reid$getIntEffectId(int potionID, @Local(argsOnly = true) SPacketEntityEffect packetIn) {
        return ((IStoredEffectInt) packetIn).getEffectInt();
    }
}
