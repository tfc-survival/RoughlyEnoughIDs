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
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Pseudo
@Mixin(Cube.class)
@Implements(@Interface(iface = INewCube.class, prefix = "int$"))
public abstract class MixinCube {
    @Shadow @Final @Nonnull private World world;

    @Shadow
    public abstract <T extends Chunk & IColumn> T getColumn();


    private static final byte errorBiomeID = (byte) Biome.REGISTRY.getIDForObject(JEID.errorBiome);
    @Nullable private int[] blockBiomeArray;

    @Overwrite(remap = false)
    public Biome getBiome(BlockPos pos) {
        if (this.blockBiomeArray == null)
            return this.getColumn().getBiome(pos, world.getBiomeProvider());

        int biomeX = Coords.blockToLocalBiome3d(pos.getX());
        int biomeY = Coords.blockToLocalBiome3d(pos.getY());
        int biomeZ = Coords.blockToLocalBiome3d(pos.getZ());
        int biomeId = this.blockBiomeArray[AddressTools.getBiomeAddress3d(biomeX, biomeY, biomeZ)];
        return Biome.getBiome(biomeId);
    }

    @Overwrite(remap = false)
    public void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome) {
        if (this.blockBiomeArray == null)
            this.blockBiomeArray = new int[64];

        this.blockBiomeArray[AddressTools.getBiomeAddress3d(localBiomeX, localBiomeY, localBiomeZ)] = Biome.REGISTRY.getIDForObject(biome);
    }

    @Nullable
    public int[] int$getBiomeArray() {
        return this.blockBiomeArray;
    }

    public void int$setBiomeArray(int[] biomeArray) {
        if (this.blockBiomeArray == null)
            this.blockBiomeArray = biomeArray;

        if (this.blockBiomeArray.length != biomeArray.length) {
            CubicChunks.LOGGER.warn("Could not set level cube biomes, array length is {} instead of {}", biomeArray.length, this.blockBiomeArray.length);
        } else {
            System.arraycopy(biomeArray, 0, this.blockBiomeArray, 0, this.blockBiomeArray.length);
        }
    }
}
