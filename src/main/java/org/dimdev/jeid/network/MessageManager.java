package org.dimdev.jeid.network;

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
}
