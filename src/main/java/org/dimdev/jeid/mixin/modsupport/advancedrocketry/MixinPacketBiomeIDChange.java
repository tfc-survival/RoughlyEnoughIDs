package org.dimdev.jeid.mixin.modsupport.advancedrocketry;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.advancedrocketry.IStoredBiomeArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;

@Mixin(value = PacketBiomeIDChange.class, remap = false)
public class MixinPacketBiomeIDChange implements IStoredBiomeArray {
    @Shadow
    Chunk chunk;
    @Unique
    int[] reid$intArray;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        reid$intArray = new int[256];
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;writeBytes([B)Lio/netty/buffer/ByteBuf;"), cancellable = true)
    private void reid$writeIntBiomeIds(ByteBuf out, CallbackInfo ci) {
        for (int biomeId : ((INewChunk) chunk).getIntBiomeArray()) {
            out.writeInt(biomeId);
        }
        ci.cancel();
    }

    @Inject(method = "readClient", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;readBytes([B)Lio/netty/buffer/ByteBuf;"), cancellable = true)
    private void reid$readIntBiomeIds(ByteBuf in, CallbackInfo ci) {
        for (int i = 0; i < 256; i++) {
            int biomeId = in.readInt();
            reid$intArray[i] = biomeId;
        }
        ci.cancel();
    }

    @Override
    public int[] getBiomeArray() {
        return reid$intArray;
    }
}
