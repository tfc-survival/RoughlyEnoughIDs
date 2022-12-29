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

import org.dimdev.jeid.JEID;
import org.dimdev.jeid.modsupport.cubicchunks.INewCube;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Pseudo
@Mixin(Cube.class)
public abstract class MixinCube implements INewCube {
    @Shadow @Final @Nonnull private World world;

    @Shadow
    public abstract <T extends Chunk & IColumn> T getColumn();


    private static final byte errorBiomeID = (byte) Biome.REGISTRY.getIDForObject(JEID.errorBiome);
    @Nullable private int[] intBlockBiomeArray = null;

    @Nullable
    @Override
    public int[] getIntBiomeArray() {
        return this.intBlockBiomeArray;
    }

    @Override
    public void setIntBiomeArray(int[] intBiomeArray) {
        if (this.intBlockBiomeArray == null)
            this.intBlockBiomeArray = intBiomeArray;
        if (this.intBlockBiomeArray.length != intBiomeArray.length)
            CubicChunks.LOGGER.warn("Could not set level cube biomes, array length is {} instead of {}", Integer.valueOf(intBiomeArray.length),
                Integer.valueOf(this.intBlockBiomeArray.length));
        System.arraycopy(intBiomeArray, 0, this.intBlockBiomeArray, 0, this.intBlockBiomeArray.length);
    }
    
    @Overwrite(remap = false)
    public Biome getBiome(BlockPos pos) {
        if (this.intBlockBiomeArray == null)
            return this.getColumn().getBiome(pos, world.getBiomeProvider());
        int biomeX = Coords.blockToBiome(pos.getX());
        int biomeZ = Coords.blockToBiome(pos.getZ());
        int biomeId = this.intBlockBiomeArray[AddressTools.getBiomeAddress(biomeX, biomeZ)] & 255;
        Biome biome = Biome.getBiome(biomeId);
        return biome;
    }

    @Overwrite(remap = false)
    public void setBiome(int localBiomeX, int localBiomeZ, Biome biome) {
        if (this.intBlockBiomeArray == null)
            this.intBlockBiomeArray = new int[64];

        this.intBlockBiomeArray[AddressTools.getBiomeAddress(localBiomeX, localBiomeZ)] = Biome.REGISTRY.getIDForObject(biome);
    }
}
