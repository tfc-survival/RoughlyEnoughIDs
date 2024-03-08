package org.dimdev.jeid.mixin.modsupport.bookshelf;

import com.llamalad7.mixinextras.sugar.Local;
import net.darkhax.bookshelf.util.WorldUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = WorldUtils.class, remap = false)
public class MixinWorldUtils {
    @Inject(method = "setBiomes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;markDirty()V", remap = true))
    private static void reid$setBiomeArray(World world, BlockPos pos, Biome biome, CallbackInfo ci, @Local Chunk chunk) {
        final int[] biomeArray = ((INewChunk) chunk).getIntBiomeArray();
        // Method calls getDirty()
        Arrays.fill(biomeArray, Biome.getIdForBiome(biome));
        if (!world.isRemote) {
            MessageManager.sendClientsBiomeArray(world, pos, Arrays.copyOf(biomeArray, biomeArray.length));
        }
    }
}
