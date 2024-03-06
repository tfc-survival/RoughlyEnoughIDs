package org.dimdev.jeid.mixin.modsupport.extrautils2;

import com.rwtema.extrautils2.biome.BiomeManip;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BiomeManip.class, remap = false)
public class MixinBiomeManip {
    /**
     * @reason Support int biome ids and rewrite {@link BiomeManip#setBiome} because it's unnecessarily complicated
     */
    @Inject(method = "setBiome", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$rewriteSetBiome(World world, Biome biome, BlockPos pos, CallbackInfo ci) {
        Chunk chunk = world.getChunk(pos);
        ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = Biome.getIdForBiome(biome);
        chunk.markDirty();
        if (!world.isRemote) {
            MessageManager.CHANNEL.sendToAllAround(
                    new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(biome)),
                    new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D)
            );
        }
        ci.cancel();
    }
}
