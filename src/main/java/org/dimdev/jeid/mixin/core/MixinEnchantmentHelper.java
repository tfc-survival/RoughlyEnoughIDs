package org.dimdev.jeid.mixin.core;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper
{
    // Captured local variables
    private static int idGEL;
    private static int idGE;
    private static int idAEM;

    @Inject(method = "getEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getShort(Ljava/lang/String;)S", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void captureNBTTagCompoundGEL(Enchantment enchID, ItemStack stack, CallbackInfoReturnable<Integer> cir, NBTTagList nbttaglist, int i, NBTTagCompound nbttagcompound)
    {
        idGEL = nbttagcompound.getInteger("id");
    }

    @ModifyArg(method = "getEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
    private static int reassignIdGEL(int id)
    {
        // Ignore the id returned by getShort()
        return idGEL;
    }

    @Inject(method = "getEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getShort(Ljava/lang/String;)S", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void captureNBTTagCompoundGE(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir, Map<Enchantment, Integer> map, NBTTagList nbttaglist, int i, NBTTagCompound nbttagcompound)
    {
        idGE = nbttagcompound.getInteger("id");
    }

    @ModifyArg(method = "getEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
    private static int reassignIdGE(int id)
    {
        // Ignore the id returned by getShort()
        return idGE;
    }

    @Inject(method = "setEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setShort(Ljava/lang/String;S)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void reassignIdSE(Map<Enchantment, Integer> enchMap, ItemStack stack, CallbackInfo ci, NBTTagList nbttaglist, Iterator<Map.Entry<Enchantment, Integer>> var3, Map.Entry<Enchantment, Integer> entry, Enchantment enchantment, int i, NBTTagCompound nbttagcompound)
    {
        // setInteger again after id is set using short
        nbttagcompound.setInteger("id", Enchantment.getEnchantmentID(enchantment));
    }

    @Inject(method = "applyEnchantmentModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getShort(Ljava/lang/String;)S", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void captureNBTTagListAEM(EnchantmentHelper.IModifier modifier, ItemStack stack, CallbackInfo ci, NBTTagList nbttaglist, int i)
    {
        idAEM = nbttaglist.getCompoundTagAt(i).getInteger("id");
    }


    @ModifyVariable(method = "applyEnchantmentModifier", at = @At(value = "STORE"), ordinal = 1)
    private static int reassignIdAEM(int id)
    {
        // Ints on LVT: (ordinal = 0) = int i, (ordinal = 1) = int j
        // Ignore the id returned by getShort()
        return idAEM;
    }
}