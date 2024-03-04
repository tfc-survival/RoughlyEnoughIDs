package org.dimdev.jeid.mixin.core.potion;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.dimdev.jeid.ducks.IStoredEffectInt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to read/write effect ids as ints. Converted from JEIDTransformer#transformSPacketEntityEffect
 *
 * @see org.dimdev.jeid.core.JEIDTransformer
 */
@Mixin(value = SPacketEntityEffect.class)
public class MixinSPacketEntityEffect implements IStoredEffectInt {
    @Unique
    private int reid$effectInt = 0;

    @Final
    @Inject(method = "<init>(ILnet/minecraft/potion/PotionEffect;)V", at = @At(value = "RETURN"))
    private void reid$initEffectInt(int entityIdIn, PotionEffect effect, CallbackInfo ci) {
        reid$effectInt = Potion.getIdFromPotion(effect.getPotion());
    }

    @Final
    @Inject(method = "readPacketData", at = @At(value = "TAIL"))
    private void reid$readEffectInt(PacketBuffer buf, CallbackInfo ci) {
        reid$effectInt = buf.readVarInt();
    }

    @Final
    @Inject(method = "writePacketData", at = @At(value = "TAIL"))
    private void reid$writeEffectInt(PacketBuffer buf, CallbackInfo ci) {
        buf.writeVarInt(reid$effectInt);
    }

    @Override
    public int getEffectInt() {
        return reid$effectInt;
    }
}
