package org.dimdev.jeid.mixin.modsupport.wyrmsofnyrus;

import com.vetpetmon.wyrmsofnyrus.world.biome.BiomeRegistry;
import com.vetpetmon.wyrmsofnyrus.world.biome.SpreadingBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(SpreadingBiome.class)
public class MixinBiomeSpread {
    // I would natively implement this considering it doesn't even need anything fancy,
    // but I must agree that making a one-off patch is much more performant than making the game
    // check for REID, though I am concerned if people are using JEID and don't know about REID...
    //
    // In 0.6 or a later 0.5.x release I'll probably figure out a way to make a native patch.
    //
    // WoN's biome alteration code is similar to Thaumcraft's and SRP's biome alteration,
    // so these patches are based on those mods' patches.
    // I keep calling Mixins patches. I see no difference.
    // Can you believe this is the first Mixin I made that actually works? -Modrome, official WoN dev
    /**
     * @author Modrome
     * @reason Patches Wyrms of Nyrus's Biome Spread compatible for WoN 0.5.x; code will need to be updated when 0.6 releases
     */
    @Overwrite(remap=false)
    public static void setBiome(World w, BlockPos pos, Biome biome) {
        if (biome != null) {
            int convX = pos.getX() & 15;
            int convZ = pos.getZ() & 15;
            ((INewChunk)w.getChunk(pos)).getIntBiomeArray()[convZ << 4 | convX] = Biome.getIdForBiome(biome);
        }

    }

    /**
     * @author Modrome
     * @reason The alternative function; code will need to be updated when 0.6 releases. This code in WoN is a default shortcut that has since been removed in 0.6
     */
    @Overwrite(remap=false)
    public static void setBiome(World w, BlockPos pos) {
        int convX = pos.getX() & 15;
        int convZ = pos.getZ() & 15;
        ((INewChunk)w.getChunk(pos)).getIntBiomeArray()[convZ << 4 | convX] = Biome.getIdForBiome(BiomeRegistry.CREEPLANDS);
    }
}
