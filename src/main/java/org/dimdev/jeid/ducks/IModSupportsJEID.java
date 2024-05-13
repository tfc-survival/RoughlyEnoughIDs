package org.dimdev.jeid.ducks;

/**
 * Duck interface for any mod that already sets up the Chunk's intBiomeArray
 * in {@link net.minecraft.world.gen.IChunkGenerator#generateChunk}.
 * If your mod uses a custom chunk generator and has explicit compat with JEID,
 * implement this in your IChunkGenerator.
 */
public interface IModSupportsJEID {
}
