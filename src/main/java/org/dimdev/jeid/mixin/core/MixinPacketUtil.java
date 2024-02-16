package org.dimdev.jeid.mixin.core;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.PacketUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.IOException;

@Mixin(PacketUtil.class)
public abstract class MixinPacketUtil {
    @Redirect(method = "writeItemStackFromClientToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeShort(I)Lio/netty/buffer/ByteBuf;", ordinal = 0))
    private static ByteBuf writeIntItemId(PacketBuffer packetBuffer, int p_writeShort_1_) {
        return packetBuffer.writeVarInt(p_writeShort_1_);
    }

    @Redirect(method = "writeItemStackFromClientToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeShort(I)Lio/netty/buffer/ByteBuf;", ordinal = 1))
    private static ByteBuf writeIntItemId1(PacketBuffer packetBuffer, int p_writeShort_1_) {
        return packetBuffer.writeVarInt(p_writeShort_1_);
    }
}
