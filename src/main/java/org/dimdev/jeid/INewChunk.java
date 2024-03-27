package org.dimdev.jeid;

/**
 * Dummy interface to act as passthrough for real INewChunk.
 * Provided to keep parity with JEID and other mod compat.
 * @see org.dimdev.jeid.ducks.INewChunk
 */
public interface INewChunk extends org.dimdev.jeid.ducks.INewChunk {
}
