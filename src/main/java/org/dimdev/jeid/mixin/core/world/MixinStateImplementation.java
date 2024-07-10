package org.dimdev.jeid.mixin.core.world;

import com.google.common.collect.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.BlockStateContainer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(StateImplementation.class)
public class MixinStateImplementation {

    private int hashCodeValue;

    @Shadow
    private ImmutableMap<IProperty<?>, Comparable<?>> properties;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void initHashCode(CallbackInfo ci) {
        hashCodeValue = properties.hashCode();
    }

    /**
     * @author hohserg
     * @reason it should just return value. kinda have no sense to @Inject
     */
    @Overwrite
    public int hashCode(){
        return hashCodeValue;
    }

}
