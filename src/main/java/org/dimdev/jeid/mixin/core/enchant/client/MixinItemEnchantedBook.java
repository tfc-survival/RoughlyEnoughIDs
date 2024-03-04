package org.dimdev.jeid.mixin.core.enchant.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ItemEnchantedBook.class)
public class MixinItemEnchantedBook {
    @ModifyArg(method = "addInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
    private int reid$getIntEnchIdForInfo(int original, @Local NBTTagCompound nbtTagCompound) {
        return nbtTagCompound.getInteger("id");
    }
}
