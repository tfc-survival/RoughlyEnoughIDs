package org.dimdev.jeid.mixin.modsupport.realisticterraingen;

import org.dimdev.jeid.ducks.IModSupportsJEID;
import org.spongepowered.asm.mixin.Mixin;
import rtg.world.gen.ChunkGeneratorRTG;

@Mixin(value = ChunkGeneratorRTG.class, remap = false)
public class MixinChunkGeneratorRTG implements IModSupportsJEID {
}
