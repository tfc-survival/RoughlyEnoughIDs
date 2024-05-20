package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(ParasiteEventWorld.class)
public abstract class MixinSpreadBiome {

    // Overwriting the biome chaning code for SRP.
    // This is just a mixin to force it to refer to REID's biome array.
    /**
     * @author roguetictac
     * @reason Make Scape and Run: Parasites compatible with REID. Refer to new chunk duck interface for mixin reasons.
     */
    @Overwrite(remap=false)
    public static void positionToParasiteBiome(World worldIn, BlockPos pos) {
        int inChunkX = pos.getX() & 15;
        int inChunkZ = pos.getZ() & 15;
        ((INewChunk)worldIn.getChunk(pos)).getIntBiomeArray()[inChunkZ << 4 | inChunkX] = Biome.getIdForBiome(SRPBiomes.biomeInfested);
    }

    /**
     * @author roguetictac
     * @reason Make Scape and Run: Parasites compatible with REID. Refer to new chunk duck interface for mixin reasons.
     */
    @Overwrite(remap=false)
    public static void positionToBiome(World worldIn, BlockPos pos) {
        int inChunkX = pos.getX() & 15;
        int inChunkZ = pos.getZ() & 15;
        ((INewChunk)worldIn.getChunk(pos)).getIntBiomeArray()[inChunkZ << 4 | inChunkX] = Biome.getIdForBiome(Biomes.PLAINS);
    }

}