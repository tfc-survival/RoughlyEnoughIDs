package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import io.github.opencubicchunks.cubicchunks.core.worldgen.generator.vanilla.VanillaCompatibilityGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.INewChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

@Pseudo
@Mixin(VanillaCompatibilityGenerator.class)
public class MixinVanillaCompatibilityGenerator {
    @Shadow private Biome[] biomes;
    @Shadow @Final @Nonnull private World world;

    
    @Overwrite(remap = false)
    public void generateColumn(Chunk column) {
        this.biomes = this.world.getBiomeProvider()
                .getBiomes(this.biomes,
                        Coords.cubeToMinBlock(column.x),
                        Coords.cubeToMinBlock(column.z),
                        Cube.SIZE, Cube.SIZE);

        INewChunk newColumn = (INewChunk) column;
        int[] aint = newColumn.getIntBiomeArray();
        for (int i = 0; i < aint.length; ++i) {
            aint[i] = Biome.getIdForBiome(this.biomes[i]);
        }
    }
}
