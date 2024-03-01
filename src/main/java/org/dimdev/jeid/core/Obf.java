package org.dimdev.jeid.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.potion.Potion;
import org.objectweb.asm.Type;

class Obf {
    protected static String NBTTagCompound;
    protected static String PotionEffect;
    protected static String SPacketEntityEffect;
    protected static String PacketBuffer;
    protected static String ItemStack;
    protected static String ITooltipFlag;
    protected static String Enchantment;
    protected static String EntityPlayer;

    public static boolean isPotionClass(String s) {
        if (s.endsWith(";")) {
            s = s.substring(1, s.length() - 1);
        }
        return s.equals(Type.getInternalName(Potion.class)) || s.equals("uz");
    }

    protected static boolean isDeobf() {
        return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    protected static void loadData() {
        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            NBTTagCompound = "net/minecraft/nbt/NBTTagCompound";
            PotionEffect = "net/minecraft/potion/PotionEffect";
            SPacketEntityEffect = "net/minecraft/network/play/server/SPacketEntityEffect";
            PacketBuffer = "net/minecraft/network/PacketBuffer";
            ItemStack = "net/minecraft/item/ItemStack";
            ITooltipFlag = "net/minecraft/client/util/ITooltipFlag";
            Enchantment = "net/minecraft/enchantment/Enchantment";
            EntityPlayer = "net/minecraft/entity/player/EntityPlayer";
        } else {
            NBTTagCompound = "fy";
            PotionEffect = "va";
            SPacketEntityEffect = "kw";
            PacketBuffer = "gy";
            ItemStack = "aip";
            ITooltipFlag = "akb";
            Enchantment = "alk";
            EntityPlayer = "aed";
        }
    }
}
