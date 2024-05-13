package org.dimdev.jeid.mixin.modsupport.biomestaff;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import p455w0rd.biomestaff.item.ItemBiomeStaff;
import p455w0rd.biomestaff.util.BiomeStaffUtil;

@Mixin(value = BiomeStaffUtil.class, remap = false)
public class MixinBiomeStaffUtil {
    /**
     * @reason Rewrite to get int biome id from NBT.
     */
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getBiomeFromStaff", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$getIntBiome(ItemStack staff, CallbackInfoReturnable<Biome> cir) {
        if (staff.hasTagCompound() && staff.getTagCompound().hasKey(ItemBiomeStaff.TAG_BIOME, Constants.NBT.TAG_INT)) {
            Biome biome = Biome.getBiome(staff.getTagCompound().getInteger(ItemBiomeStaff.TAG_BIOME));
            cir.setReturnValue(biome);
            return;
        }
        cir.setReturnValue(null);
    }

    /**
     * @reason Rewrite to construct NBT based on int biome id.
     */
    @Inject(method = "createTagForBiome", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$createIntTag(Biome biome, CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound tag = new NBTTagCompound();
        int biomeId = Biome.getIdForBiome(biome);
        tag.setInteger(ItemBiomeStaff.TAG_BIOME, biomeId);
        cir.setReturnValue(tag);
    }
}
