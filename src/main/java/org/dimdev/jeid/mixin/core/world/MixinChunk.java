package org.dimdev.jeid.mixin.core.world;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.biome.BiomeError;
import org.dimdev.jeid.ducks.INewChunk;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Chunk.class)
public class MixinChunk implements INewChunk {
    @Unique
    private static final byte ERROR_BIOME_ID = (byte) Biome.REGISTRY.getIDForObject(BiomeError.getInstance());
    @Unique
    private static final byte[] EMPTY_BLOCK_BIOME_ARRAY = new byte[256];
    @Unique
    private final int[] intBiomeArray = generateIntBiomeArray();

    @Unique
    private static int[] generateIntBiomeArray() {
        int[] arr = new int[256];
        Arrays.fill(arr, -1);
        return arr;
    }

    @Override
    public int[] getIntBiomeArray() {
        return intBiomeArray;
    }

    @Override
    public void setIntBiomeArray(int[] intBiomeArray) {
        System.arraycopy(intBiomeArray, 0, this.intBiomeArray, 0, this.intBiomeArray.length);
    }

    @SuppressWarnings("unused")
    @Inject(method = "getBiomeArray", at = @At(value = "RETURN"), cancellable = true)
    private void reid$returnErrorBiomeArray(CallbackInfoReturnable<byte[]> cir) {
        byte[] arr = new byte[256];
        Arrays.fill(arr, ERROR_BIOME_ID);
        cir.setReturnValue(arr);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getBiome", at = @At(value = "STORE", ordinal = 0), name = "k")
    private int reid$fromIntBiomeArray(int original, @Local(name = "i") int i, @Local(name = "j") int j) {
        return this.intBiomeArray[j << 4 | i];
    }

    @Inject(method = "getBiome", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I"))
    private void reid$toIntBiomeArray(CallbackInfoReturnable<Biome> cir, @Local(name = "i") int i, @Local(name = "j") int j, @Local(name = "k") int k) {
        this.intBiomeArray[j << 4 | i] = k;
    }

    /**
     * @reason Disable default biome array write logic.
     */
    @Redirect(method = "getBiome", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;blockBiomeArray:[B", opcode = Opcodes.GETFIELD, ordinal = 1))
    private byte[] reid$defaultWriteBiomeArray(Chunk instance) {
        return EMPTY_BLOCK_BIOME_ARRAY;
    }
}
