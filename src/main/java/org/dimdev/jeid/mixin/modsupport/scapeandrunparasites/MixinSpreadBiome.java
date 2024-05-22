package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.llamalad7.mixinextras.sugar.Local;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ParasiteEventWorld.class, remap = false)
public class MixinSpreadBiome {
    /**
     * @reason Use REID message to immediately re-render changes on client
     */
    @Redirect(method = "SpreadBiome", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToDimension(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;I)V"))
    private static void reid$sendSpreadBiomeChange(SimpleNetworkWrapper instance, IMessage message, int dimensionId, World worldIn, BlockPos pos,
                                                   @Local(ordinal = 1) BlockPos convertPos) {
        MessageManager.sendClientsBiomePosChange(worldIn, convertPos, Biome.getIdForBiome(SRPBiomes.biomeInfested));
    }

    /**
     * @reason Use REID message to immediately re-render changes on client
     */
    @Redirect(method = "killBiome", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToDimension(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;I)V"))
    private static void reid$sendKillBiomeChange(SimpleNetworkWrapper instance, IMessage message, int dimensionId, World worldIn, BlockPos pos,
                                                 @Local(ordinal = 1) BlockPos convertPos) {
        Biome original = worldIn.getBiomeProvider().getBiome(pos, Biomes.PLAINS);
        MessageManager.sendClientsBiomePosChange(worldIn, convertPos, Biome.getIdForBiome(original));
    }

    /**
     * @author roguetictac, jchung01
     * @reason Support int biome id for spreading infected biome.
     */
    @Inject(method = "positionToParasiteBiome", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$parasiteToIntBiomeArray(World worldIn, BlockPos pos, CallbackInfo ci) {
        Chunk chunk = worldIn.getChunk(pos);
        int inChunkX = pos.getX() & 15;
        int inChunkZ = pos.getZ() & 15;
        chunk.markDirty();
        ((INewChunk) chunk).getIntBiomeArray()[inChunkZ << 4 | inChunkX] = Biome.getIdForBiome(SRPBiomes.biomeInfested);
        ci.cancel();
    }

    /**
     * @author roguetictac, jchung01
     * @reason Support int biome id for resetting infected biome.
     */
    @Inject(method = "positionToBiome", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$plainsToIntBiomeArray(World worldIn, BlockPos pos, CallbackInfo ci) {
        Biome original = worldIn.getBiomeProvider().getBiome(pos, Biomes.PLAINS);
        Chunk chunk = worldIn.getChunk(pos);
        int inChunkX = pos.getX() & 15;
        int inChunkZ = pos.getZ() & 15;
        chunk.markDirty();
        ((INewChunk) chunk).getIntBiomeArray()[inChunkZ << 4 | inChunkX] = Biome.getIdForBiome(original);
        ci.cancel();
    }
}