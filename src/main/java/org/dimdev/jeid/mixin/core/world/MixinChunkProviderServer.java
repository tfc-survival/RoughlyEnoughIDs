package org.dimdev.jeid.mixin.core.world;

import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

import com.llamalad7.mixinextras.sugar.Local;
import org.dimdev.jeid.ducks.ICustomBiomesForGeneration;
import org.dimdev.jeid.ducks.IModSupportsJEID;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {
    @Unique
    private final Biome[] reusableBiomeList = new Biome[256];
    @Shadow
    @Final
    public IChunkGenerator chunkGenerator;
    @Shadow
    @Final
    public WorldServer world;

    /**
     * @reason Initialize biome array after any calls to {@link net.minecraft.world.gen.IChunkGenerator#generateChunk}.
     * This guarantees the correct biomes even for modded chunk generators.
     */
    @Inject(method = "provideChunk", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/IChunkGenerator;generateChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private void reid$initializeBiomeArray(int x, int z, CallbackInfoReturnable<Chunk> cir, @Local Chunk chunk) {
        if (this.chunkGenerator instanceof IModSupportsJEID) {
            return;
        }
        Biome[] biomes;
        if (this.chunkGenerator instanceof ICustomBiomesForGeneration) {
            // Some chunk generators modify the biomes beyond those returned by the BiomeProvider.
            biomes = ((ICustomBiomesForGeneration) this.chunkGenerator).getBiomesForGeneration();
        }
        else {
            biomes = world.getBiomeProvider().getBiomes(reusableBiomeList, x * 16, z * 16, 16, 16);
        }
        INewChunk newChunk = (INewChunk) chunk;
        int[] intBiomeArray = newChunk.getIntBiomeArray();
        for (int i = 0; i < intBiomeArray.length; ++i) {
            intBiomeArray[i] = Biome.getIdForBiome(biomes[i]);
        }
    }
}
