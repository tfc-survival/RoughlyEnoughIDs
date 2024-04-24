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
        CHANNEL.registerMessage(BiomeChunkChangeMessage.Handler.class, BiomeChunkChangeMessage.class, 0, Side.CLIENT);
        CHANNEL.registerMessage(BiomePositionChangeMessage.Handler.class, BiomePositionChangeMessage.class, 1, Side.CLIENT);
    }

    public static void sendClientsBiomePosChange(World world, BlockPos pos, int biomeId) {
        MessageManager.CHANNEL.sendToAllTracking(
                new BiomePositionChangeMessage(pos.getX(), pos.getZ(), biomeId),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0.0D) // Range ignored
        );
    }

    public static void sendClientsBiomeChunkChange(World world, BlockPos pos, int[] biomeArr) {
        MessageManager.CHANNEL.sendToAllTracking(
                new BiomeChunkChangeMessage(pos.getX() >> 4, pos.getZ() >> 4, biomeArr), // Expects chunkX/Z
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0.0D) // Range ignored
        );
    }


}
