package org.dimdev.jeid.mixin.modsupport.journeymap;

import journeymap.client.model.ChunkMD;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkMD.class, remap = false)
public abstract class MixinChunkMD {
    @Shadow
    public abstract Chunk getChunk();

    /**
     * @author Runemoro, ZombieHDGaming
     * @reason Support int biome ids and rewrite because ModifyVariable can't find target
     */
    @Inject(method = "getBiome", at = @At(value = "HEAD"), cancellable = true)
    private void reid$rewriteGetBiome(BlockPos pos, CallbackInfoReturnable<Biome> cir) {
        Chunk chunk = this.getChunk();
        int[] biomeArray = ((INewChunk) chunk).getIntBiomeArray();
        int biomeId = biomeArray[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF];
        if (biomeId == 0xFFFFFFFF) {
            Biome biome = chunk.getWorld().getBiomeProvider().getBiome(pos, null);

            if (biome == null) {
                cir.setReturnValue(null);
            }
            biomeId = Biome.getIdForBiome(biome);
            // Client-side only
            biomeArray[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = biomeId;
        }

        cir.setReturnValue(Biome.getBiome(biomeId));
    }
}
