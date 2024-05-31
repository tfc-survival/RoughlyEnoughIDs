package org.dimdev.jeid.mixin.modsupport.wyrmsofnyrus;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.llamalad7.mixinextras.sugar.Local;
import com.vetpetmon.wyrmsofnyrus.invasion.HiveCreepSpreadFurther;
import com.vetpetmon.wyrmsofnyrus.world.biome.BiomeRegistry;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HiveCreepSpreadFurther.class, remap = false)
public class MixinHiveCreepSpreadFurther {
    /**
     * @reason Use REID message to immediately re-render changes on client
     */
    @Redirect(method = "decay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToDimension(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;I)V"))
    private static void reid$sendSpreadBiomeChange(SimpleNetworkWrapper instance, IMessage message, int dimensionId, BlockPos pos, World world,
                                                   @Local(ordinal = 2) BlockPos spreadPos) {
        MessageManager.sendClientsBiomePosChange(world, spreadPos, Biome.getIdForBiome(BiomeRegistry.CREEPLANDS));
    }
}
