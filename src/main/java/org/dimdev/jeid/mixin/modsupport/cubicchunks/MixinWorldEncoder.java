package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(targets = "io.github.opencubicchunks.cubicchunks.core.network.WorldEncoder", remap = false)
public class MixinWorldEncoder {
    @Redirect(method = "lambda$encodeCubes$0", at = @At(value = "INVOKE", target = "Lio/github/opencubicchunks/cubicchunks/core/world/cube/Cube;getBiomeArray()[B"))
    private static byte[] reid$encodeCheckEmptiness(Cube instance) {
        if (((INewCube) instance).getBiomeArray() != null) {
            return new byte[0];
        }
        return null;
    }

    @Inject(method = "lambda$encodeCubes$5", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$encodeBiomes(PacketBuffer out, Cube cube, CallbackInfo ci) {
        int[] biomes = ((INewCube) cube).getBiomeArray();
        if (biomes != null) {
            for (int biome : biomes) {
                out.writeInt(biome);
            }
        }
        ci.cancel();
    }

    @Redirect(method = "encodeColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeBytes([B)Lio/netty/buffer/ByteBuf;", remap = true))
    private static ByteBuf reid$writeColumnBiomeArray(PacketBuffer instance, byte[] oldBiomeArray, PacketBuffer out, Chunk column) {
        int[] biomes = ((INewChunk) column).getIntBiomeArray();
        //JEID.LOGGER.info("current biome: {}", biomes[128]);
        for (int i = 0; i < 256; i++) {
            out.writeInt(biomes[i]);
        }
        return out;
    }

    @Redirect(method = "decodeColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readBytes([B)Lio/netty/buffer/ByteBuf;", remap = true))
    private static ByteBuf reid$readColumnBiomeArray(PacketBuffer instance, byte[] oldBiomeArray, PacketBuffer in, Chunk column) {
        int[] biomes = new int[256];
        for (int i = 0; i < 256; i++) {
            biomes[i] = in.readInt();
        }
        ((INewChunk) column).setIntBiomeArray(biomes);
        return in;
    }

    @Inject(method = "decodeCube",
            slice = @Slice(
                    id = "lastLoop",
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;recalculateRefCounts()V", remap = true)
            ), at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;", slice = "lastLoop"), cancellable = true
    )
    private static void reid$decodeBiomes(PacketBuffer in, List<Cube> cubes, CallbackInfo ci, @Local(ordinal = 0) int i) {
        int[] blockBiomeArray = new int[Coords.BIOMES_PER_CUBE];
        for (int j = 0; j < 64; j++)
            blockBiomeArray[j] = in.readInt();
        ((INewCube) cubes.get(i)).setBiomeArray(blockBiomeArray);
        ci.cancel();
    }

    /**
     * @author Exsolutus
     * @reason Encode int array size
     */
    @Overwrite
    static int getEncodedSize(Chunk column) {
        // Could probably just inline the length
        return (((INewChunk) column).getIntBiomeArray().length * Integer.BYTES) + (Cube.SIZE * Cube.SIZE * Integer.BYTES);
    }

    /**
     * Intercepts iterator of for-each loop
     *
     * @reason Encode int array sizes for each cube
     */
    //
    @Inject(method = "getEncodedSize(Ljava/util/Collection;)I", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;", ordinal = 1))
    private static void reid$encodeCubeBiomeSizes(CallbackInfoReturnable<Integer> cir, @Local LocalIntRef size, @Local Iterator<Cube> iterator) {
        while (iterator.hasNext()) {
            Cube cube = iterator.next();
            int[] biomeArray = ((INewCube) cube).getBiomeArray();
            if (biomeArray == null)
                continue;
            size.set(size.get() + (Integer.BYTES * biomeArray.length));
        }
        // Original for-each will now be skipped as iterator has been consumed
    }
}
