package org.dimdev.jeid.mixin.modsupport.hammercore;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.utils.WorldLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldLocation.class, remap = false)
public class MixinWorldLocation {
    @Shadow
    private World world;
    @Shadow
    private BlockPos pos;

    @Redirect(method = "setBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V", remap = true))
    private void reid$toIntBiomeArray(Chunk instance, byte[] biomeArray, Biome biome) {
        ((INewChunk) instance).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = Biome.getIdForBiome(biome);
        instance.markDirty();
    }

    @Redirect(method = "setBiome", at = @At(value = "INVOKE", target = "Lcom/zeitheron/hammercore/net/HCNet;sendToAllAround(Lcom/zeitheron/hammercore/net/IPacket;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V"))
    private void reid$sendBiomeMessage(HCNet instance, IPacket packet, NetworkRegistry.TargetPoint point, Biome biome) {
        MessageManager.sendClientsBiomePosChange(world, pos, Biome.getIdForBiome(biome));
    }
}
