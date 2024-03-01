package org.dimdev.jeid.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.dimdev.jeid.ASMException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Since 2.0.9: Cleaned up obfuscated code.
 * <p>
 * This class was borrowed from Zabi94's MaxPotionIDExtender
 * under MIT License and with full help of Zabi. All credit in this class goes to Zabi
 * and his incredible work on figuring out how to make this work and helping out.
 * <p>
 * <a href="https://github.com/zabi94/MaxPotionIDExtender">MaxPotionIDExtender by Zabi94</a>
 */
public class JEIDTransformer implements IClassTransformer {
    public static RegistryNamespaced<ResourceLocation, Potion> REGISTRY;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.network.NetHandlerPlayClient")) {
            return transformNetHandlerPlayClient(basicClass);
        }
        if (transformedName.equals("net.minecraft.potion.PotionEffect")) {
            return transformPotionEffect(basicClass);
        }
        if (transformedName.equals("net.minecraft.network.play.server.SPacketEntityEffect")) {
            return transformSPacketEntityEffect(basicClass);
        }
        if (transformedName.equals("net.minecraft.network.play.server.SPacketRemoveEntityEffect")) {
            return transformSPacketRemoveEntityEffect(basicClass);
        }
        if (transformedName.equals("net.minecraft.item.ItemStack")) {
            return transformItemStack(basicClass);
        }
        return basicClass;
    }

    private static MethodNode locateMethod(ClassNode cn, String desc, String... namesIn) {
        return cn.methods.stream()
                .filter(n -> n.desc.equals(desc) && anyMatch(namesIn, n.name))
                .findAny().orElseThrow(() -> new ASMException(getNames(namesIn) + ": " + desc + " cannot be found in " + cn.name, cn));
    }

    private static boolean anyMatch(String[] pool, String match) {
        for (String s : pool) {
            if (s.equals(match)) {
                return true;
            }
        }
        return false;
    }

    private static String getNames(String[] pool) {
        StringBuilder sb = new StringBuilder();
        for (String s : pool) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private static AbstractInsnNode locateTargetInsn(MethodNode mn, Predicate<AbstractInsnNode> filter) {
        AbstractInsnNode target = null;
        Iterator<AbstractInsnNode> i = mn.instructions.iterator();
        while (i.hasNext() && target == null) {
            AbstractInsnNode n = i.next();
            if (filter.test(n)) {
                target = n;
            }
        }
        if (target == null) {
            throw new ASMException("Can't locate target instruction in " + mn.name, mn);
        }
        return target;
    }

    private byte[] transformItemStack(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        String enchantment = "net/minecraft/enchantment/Enchantment";
        String nbtTagCompound = "net/minecraft/nbt/NBTTagCompound";
        // Remove short cast
        MethodNode mn = locateMethod(cn, "(L" + enchantment + ";I)V", "addEnchantment", "func_77966_a");
        mn.instructions.remove(locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.I2S));

        // Redirect setShort to setInt
        AbstractInsnNode target2 = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && n.getPrevious().getPrevious().getPrevious().getOpcode() == Opcodes.LDC && ((LdcInsnNode) n.getPrevious().getPrevious().getPrevious()).cst.toString().equals("id"));
        mn.instructions.insertBefore(target2, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "setInteger" : "func_74768_a"), "(Ljava/lang/String;I)V", false));
        mn.instructions.remove(target2);

        // Redirect getShort to getInt
        if (FMLLaunchHandler.side().isClient()) {
            MethodNode mn2 = locateMethod(cn, "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;", "getTooltip", "func_82840_a");
            AbstractInsnNode target = locateTargetInsn(mn2, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && n.getPrevious().getOpcode() == Opcodes.LDC && ((LdcInsnNode) n.getPrevious()).cst.toString().equals("id"));
            mn.instructions.insertBefore(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "getInteger" : "func_74762_e"), "(Ljava/lang/String;)I", false));
            mn.instructions.remove(target);
        }

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    // TODO: Convert to mixin
    private byte[] transformSPacketRemoveEntityEffect(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // Redirect readUnsignedByte (aka short) to readInt
        String packetBuffer = "net/minecraft/network/PacketBuffer";
        String descPacketData = "(L" + packetBuffer + ";)V";
        MethodNode rpd = locateMethod(cn, descPacketData, "readPacketData", "func_148837_a");
        AbstractInsnNode target = locateTargetInsn(rpd, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) n).name.equals("readUnsignedByte"));
        rpd.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, packetBuffer, "readInt", "()I", false));
        rpd.instructions.remove(target);

        // Redirect writeByte to writeInt
        MethodNode wpd = locateMethod(cn, descPacketData, "writePacketData", "func_148840_b");
        target = locateTargetInsn(wpd, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) n).name.equals("writeByte"));
        wpd.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, packetBuffer, "writeInt", "(I)Lio/netty/buffer/ByteBuf;", false));
        wpd.instructions.remove(target);

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    // TODO: Convert to mixin
    private byte[] transformSPacketEntityEffect(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        String potionEffect = "net/minecraft/potion/PotionEffect";
        String sPacketEntityEffect = "net/minecraft/network/play/server/SPacketEntityEffect";
        String packetBuffer = "net/minecraft/network/PacketBuffer";
        String descPacketData = "(L" + packetBuffer + ";)V";
        // Add new field, int effectInt
        cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "effectInt", "I", null, 0));

        MethodNode mnInit = locateMethod(cn, "(IL" + potionEffect + ";)V", "<init>");
        Iterator<AbstractInsnNode> i = mnInit.instructions.iterator();
        AbstractInsnNode targetNode = null;
        int line = 0;
        while (i.hasNext() && targetNode == null) {
            AbstractInsnNode node = i.next();
            if (node instanceof LineNumberNode) {
                if (line == 1) {
                    targetNode = node;
                }
                line++;
            }
        }

        if (targetNode == null) {
            throw new RuntimeException("Can't find target node for SPacketEntityEffect constructor");
        }

        // Initialize effectInt in constructors
        // These are reversed, they get pushed down the stack
        mnInit.instructions.insert(targetNode, new FieldInsnNode(Opcodes.PUTFIELD, sPacketEntityEffect, "effectInt", "I"));
        mnInit.instructions.insert(targetNode, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(this.getClass()), "getIdFromPotEffect", "(L" + potionEffect + ";)I", false));
        mnInit.instructions.insert(targetNode, new VarInsnNode(Opcodes.ALOAD, 2));
        mnInit.instructions.insert(targetNode, new VarInsnNode(Opcodes.ALOAD, 0));

        MethodNode mnEmptyInit = locateMethod(cn, "()V", "<init>");
        AbstractInsnNode tgt = locateTargetInsn(mnEmptyInit, n -> n.getOpcode() == Opcodes.RETURN);
        mnEmptyInit.instructions.insertBefore(tgt, new VarInsnNode(Opcodes.ALOAD, 0));
        mnEmptyInit.instructions.insertBefore(tgt, new LdcInsnNode(0));
        mnEmptyInit.instructions.insertBefore(tgt, new FieldInsnNode(Opcodes.PUTFIELD, sPacketEntityEffect, "effectInt", "I"));

        // Patch readPacketData: this.effectInt = buf.readVarInt();
        MethodNode mnReadPacket = locateMethod(cn, descPacketData, "readPacketData", "func_148837_a");
        AbstractInsnNode target = locateTargetInsn(mnReadPacket, n -> n.getOpcode() == Opcodes.RETURN).getPrevious().getPrevious();
        mnReadPacket.instructions.insertBefore(target, new VarInsnNode(Opcodes.ALOAD, 0));
        mnReadPacket.instructions.insertBefore(target, new VarInsnNode(Opcodes.ALOAD, 1));
        mnReadPacket.instructions.insertBefore(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, packetBuffer, (JEIDLoadingPlugin.isDeobf ? "readVarInt" : "func_150792_a"), "()I", false));
        mnReadPacket.instructions.insertBefore(target, new FieldInsnNode(Opcodes.PUTFIELD, sPacketEntityEffect, "effectInt", "I"));

        // Patch writePacketData: buf.writeVarInt(effectInt);
        MethodNode mnWritePacket = locateMethod(cn, descPacketData, "writePacketData", "func_148840_b");
        AbstractInsnNode wpTarget = locateTargetInsn(mnWritePacket, n -> n.getOpcode() == Opcodes.RETURN).getPrevious().getPrevious();
        mnWritePacket.instructions.insertBefore(wpTarget, new VarInsnNode(Opcodes.ALOAD, 1));
        mnWritePacket.instructions.insertBefore(wpTarget, new VarInsnNode(Opcodes.ALOAD, 0));
        mnWritePacket.instructions.insertBefore(wpTarget, new FieldInsnNode(Opcodes.GETFIELD, sPacketEntityEffect, "effectInt", "I"));
        mnWritePacket.instructions.insertBefore(wpTarget, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, packetBuffer, (JEIDLoadingPlugin.isDeobf ? "writeVarInt" : "func_150787_b"), "(I)L" + packetBuffer + ";", false));
        mnWritePacket.instructions.insertBefore(wpTarget, new InsnNode(Opcodes.POP));

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformPotionEffect(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        String nbtTagCompound = "net/minecraft/nbt/NBTTagCompound";
        String potionEffect = "net/minecraft/potion/PotionEffect";
        MethodNode mn = locateMethod(cn, "(L" + nbtTagCompound + ";)L" + nbtTagCompound + ";", "writeCustomPotionEffectToNBT", "func_82719_a");
        AbstractInsnNode ant = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.I2B);
        MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "setInteger" : "func_74768_a"), "(Ljava/lang/String;I)V", false);
        // Redirect setByte to setInteger
        mn.instructions.remove(ant.getNext());
        mn.instructions.insert(ant, call);
        // Remove byte cast
        mn.instructions.remove(ant);

        MethodNode mn2 = locateMethod(cn, "(L" + nbtTagCompound + ";)L" + potionEffect + ";", "readCustomPotionEffectFromNBT", "func_82722_b");
        AbstractInsnNode ant2 = locateTargetInsn(mn2, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL);
        // Remove bitwise operation
        mn2.instructions.remove(ant2.getNext());
        mn2.instructions.remove(ant2.getNext());
        // Redirect getByte to getInteger
        mn2.instructions.insert(ant2, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "getInteger" : "func_74762_e"), "(Ljava/lang/String;)I", false));
        mn2.instructions.remove(ant2);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    // TODO: Convert to mixin (@ModifyArg)
    private byte[] transformNetHandlerPlayClient(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        String sPacketEntityEffect = "net/minecraft/network/play/server/SPacketEntityEffect";
        MethodNode mn = locateMethod(cn, "(L" + sPacketEntityEffect + ";)V", "handleEntityEffect", "func_147260_a");
        AbstractInsnNode target = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.SIPUSH);
        mn.instructions.remove(target.getPrevious());
        mn.instructions.remove(target.getNext());
        // Redirect getEffectID to effectInt
        mn.instructions.insertBefore(target, new FieldInsnNode(Opcodes.GETFIELD, sPacketEntityEffect, "effectInt", "I"));
        // Remove bitwise operation
        mn.instructions.remove(target);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    @SuppressWarnings("unused")
    public static int getIdFromPotEffect(PotionEffect pe) {
        return REGISTRY.getIDForObject(pe.getPotion());
    }
}

