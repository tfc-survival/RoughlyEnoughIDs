package org.dimdev.jeid.mixin.core.world;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StateImplementation.class)
public class MixinStateImplementation {

    @Unique
    private int hashCodeValue;

    @Shadow
    @Final
    private Block block;

    @Shadow
    @Final
    private ImmutableMap<IProperty<?>, Comparable<?>> properties;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void initHashCode(CallbackInfo ci) {
        hashCodeValue = Objects.hash(block, properties);
    }

    /**
     * @author hohserg
     * @reason it should just return value. kinda have no sense to @Inject
     */
    @Overwrite
    public int hashCode() {
        return hashCodeValue;
    }

}
