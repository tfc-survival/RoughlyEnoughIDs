package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.api.world.IColumn;
import io.github.opencubicchunks.cubicchunks.core.CubicChunks;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.biome.BiomeError;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(value = Cube.class, remap = false)
@Implements(@Interface(iface = INewCube.class, prefix = "int$"))
public abstract class MixinCube {
    private static final byte ERROR_BIOME_ID = (byte) Biome.REGISTRY.getIDForObject(BiomeError.getInstance());
    @Shadow
    @Final
    @Nonnull
    private World world;
    @Unique
    @Nullable
    private int[] intBiomeArray;

    @SuppressWarnings("deprecation")
    @Shadow
    public abstract <T extends Chunk & IColumn> T getColumn();

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    public Biome getBiome(BlockPos pos) {
        if (this.intBiomeArray == null) {
            return this.getColumn().getBiome(pos, world.getBiomeProvider());
        }
        int biomeX = Coords.blockToLocalBiome3d(pos.getX());
        int biomeY = Coords.blockToLocalBiome3d(pos.getY());
        int biomeZ = Coords.blockToLocalBiome3d(pos.getZ());
        int biomeId = this.intBiomeArray[AddressTools.getBiomeAddress3d(biomeX, biomeY, biomeZ)];
        return Biome.getBiome(biomeId);
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    public void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome) {
        if (this.intBiomeArray == null)
            this.intBiomeArray = new int[64];

        this.intBiomeArray[AddressTools.getBiomeAddress3d(localBiomeX, localBiomeY, localBiomeZ)] = Biome.REGISTRY.getIDForObject(biome);
    }

    @Nullable
    public int[] int$getBiomeArray() {
        return this.intBiomeArray;
    }

    public void int$setBiomeArray(int[] biomeArray) {
        if (this.intBiomeArray == null)
            this.intBiomeArray = biomeArray;

        if (this.intBiomeArray.length != biomeArray.length) {
            CubicChunks.LOGGER.warn("Could not set level cube biomes, array length is {} instead of {}", biomeArray.length, this.intBiomeArray.length);
        } else {
            System.arraycopy(biomeArray, 0, this.intBiomeArray, 0, this.intBiomeArray.length);
        }
    }
}
