package org.dimdev.jeid.mixin.modsupport.creepingnether;

import com.cutievirus.creepingnether.entity.CorruptorAbstract;
import com.llamalad7.mixinextras.sugar.Local;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.net.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CorruptorAbstract.class, remap = false)
public abstract class MixinCorruptorAbstract {
    @Shadow public abstract Biome getBiome();

    @Inject(method = "corruptBiome", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z", opcode = Opcodes.GETFIELD, remap = true))
    private void reid$toIntBiomeArray(World world, BlockPos pos, CallbackInfo ci, @Local Chunk chunk) {
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 15) << 4 | pos.getX() & 15] = Biome.getIdForBiome(getBiome());
    }

    @Redirect(method = "corruptBiome", at = @At(value = "INVOKE", target = "Lcom/cutievirus/creepingnether/entity/MessageCorruptBiome;sendMessage(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lcom/cutievirus/creepingnether/entity/CorruptorAbstract;)V"))
    private void reid$sendBiomeMessage(World world, BlockPos pos, CorruptorAbstract corruptor) {
        MessageManager.CHANNEL.sendToAllAround(
                new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(getBiome())),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D)
        );
    }
}
