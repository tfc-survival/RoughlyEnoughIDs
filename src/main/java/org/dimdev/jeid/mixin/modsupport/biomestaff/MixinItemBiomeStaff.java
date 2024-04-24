package org.dimdev.jeid.mixin.modsupport.biomestaff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;

import com.llamalad7.mixinextras.sugar.Local;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import p455w0rd.biomestaff.init.ModNetworking;
import p455w0rd.biomestaff.item.ItemBiomeStaff;
import p455w0rd.biomestaff.network.PacketSyncBiomeStaff;

@Mixin(value = ItemBiomeStaff.class)
public class MixinItemBiomeStaff {
    @ModifyArg(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;hasKey(Ljava/lang/String;I)Z"), index = 1)
    private int reid$checkIntNBTKey(int original) {
        return Constants.NBT.TAG_INT;
    }

    /**
     * @reason Rewrite sneak use logic to save int biome id.
     */
    @Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;", ordinal = 0), cancellable = true)
    private void reid$sneakUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir,
                               @Local ItemStack heldStack, @Local NBTTagCompound tag) {
        Chunk chunk = world.getChunk(pos);
        int biomeId = ((INewChunk) chunk).getIntBiomeArray()[(pos.getZ() & 0xF) << 4 | (pos.getX() & 0xF)];
        if (!tag.hasKey(ItemBiomeStaff.TAG_BIOME, Constants.NBT.TAG_INT) || tag.getInteger(ItemBiomeStaff.TAG_BIOME) != biomeId) {
            tag.setInteger(ItemBiomeStaff.TAG_BIOME, biomeId);
            heldStack.setTagCompound(tag);
            ModNetworking.getInstance().sendTo(new PacketSyncBiomeStaff(heldStack.getTagCompound()), (EntityPlayerMP)player);
        }
        cir.setReturnValue(EnumActionResult.SUCCESS);
    }

    /**
     * @reason Rewrite biome application logic to use int biome id.
     */
    @Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getByte(Ljava/lang/String;)B", ordinal = 1), cancellable = true)
    private void reid$applyBiome(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir,
                                 @Local ItemStack heldStack, @Local NBTTagCompound tag, @Local(ordinal = 1) int rad) {
        int toBiomeId = tag.getInteger(ItemBiomeStaff.TAG_BIOME);
        for(int ix = pos.getX() - rad; ix <= pos.getX() + rad; ++ix) {
            for(int iz = pos.getZ() - rad; iz <= pos.getZ() + rad; ++iz) {
                Chunk chunk = world.getChunk(new BlockPos(ix, pos.getY(), iz));
                int[] biomeArray = ((INewChunk) chunk).getIntBiomeArray();
                int biomeIdAtPos = biomeArray[(iz & 0xF) << 4 | (ix & 0xF)];
                if (biomeIdAtPos != toBiomeId) {
                    chunk.markDirty();
                    biomeArray[(iz & 0xF) << 4 | (ix & 0xF)] = toBiomeId;
                }
            }
        }
        MessageManager.sendClientsBiomeAreaChange(world, pos, rad, toBiomeId);
        cir.setReturnValue(EnumActionResult.SUCCESS);
    }
}
