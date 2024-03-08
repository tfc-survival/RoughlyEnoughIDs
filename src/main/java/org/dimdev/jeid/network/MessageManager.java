package org.dimdev.jeid.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.dimdev.jeid.JEID;

public class MessageManager {
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(JEID.MODID);

    public static void init() {
        CHANNEL.registerMessage(BiomeArrayMessage.Handler.class, BiomeArrayMessage.class, 0, Side.CLIENT);
        CHANNEL.registerMessage(BiomeChangeMessage.Handler.class, BiomeChangeMessage.class, 1, Side.CLIENT);
    }

    public static void sendClientsBiomeChange(World world, BlockPos pos, int biomeId) {
        MessageManager.CHANNEL.sendToAllTracking(
                new BiomeChangeMessage(pos.getX(), pos.getZ(), biomeId),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0.0D) // Range ignored
        );
    }

    public static void sendClientsBiomeArray(World world, BlockPos pos, int[] biomeArr) {
        MessageManager.CHANNEL.sendToAllTracking(
                new BiomeArrayMessage(pos.getX(), pos.getZ(), biomeArr),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0.0D) // Range ignored
        );
    }
}
