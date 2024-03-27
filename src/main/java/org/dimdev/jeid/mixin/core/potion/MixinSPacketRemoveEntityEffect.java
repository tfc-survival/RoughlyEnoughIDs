package org.dimdev.jeid.mixin.core.potion;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin to read potion ids as ints. Converted from JEIDTransformer#transformSPacketRemoveEntityEffect
 *
 * @see org.dimdev.jeid.core.JEIDTransformer
 */
@Mixin(value = SPacketRemoveEntityEffect.class)
public class MixinSPacketRemoveEntityEffect {
    @Final
    @ModifyArg(method = "readPacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potion;getPotionById(I)Lnet/minecraft/potion/Potion;"), index = 0)
    private int reid$readIntPotionId(int original, @Local(argsOnly = true) PacketBuffer buf) {
        return buf.readInt();
    }

    @Final
    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeByte(I)Lio/netty/buffer/ByteBuf;"))
    private ByteBuf reid$writeIntPotionId(PacketBuffer instance, int potionId) {
        return instance.writeInt(potionId);
    }
}
