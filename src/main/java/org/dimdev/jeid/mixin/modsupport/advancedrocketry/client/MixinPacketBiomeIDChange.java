package org.dimdev.jeid.mixin.modsupport.advancedrocketry.client;

import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.advancedrocketry.IStoredBiomeArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;

@Mixin(value = PacketBiomeIDChange.class)
public abstract class MixinPacketBiomeIDChange implements IStoredBiomeArray {
    @Redirect(method = "executeClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V", remap = true), remap = false)
    private void reid$setIntBiomeIdsClient(Chunk chunk, byte[] biomeArray) {
        ((INewChunk) chunk).setIntBiomeArray(getBiomeArray());
    }
}
