package org.dimdev.jeid.mixin.modsupport.naturescompass;

import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.biome.Biome;
import org.dimdev.jeid.biome.BiomeError;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BiomeUtils.class, remap = false)
public class MixinBiomeUtils {
    @ModifyReturnValue(method = "biomeIsBlacklisted", at = @At(value = "RETURN"))
    private static boolean reid$hideErrorBiome(boolean original, Biome biome) {
        return (biome instanceof BiomeError) || original;
    }
}
