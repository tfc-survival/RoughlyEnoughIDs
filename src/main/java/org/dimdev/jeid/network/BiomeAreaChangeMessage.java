package org.dimdev.jeid.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import org.dimdev.jeid.ducks.INewChunk;

public class BiomeAreaChangeMessage implements IMessage {
    private int x;
    private int z;
    private int radius;
    private int biomeId;

    public BiomeAreaChangeMessage() {}

    public BiomeAreaChangeMessage(int x, int z, int radius, int biomeId) {
        this.x = x;
        this.z = z;
        this.radius = radius;
        this.biomeId = biomeId;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        x = packetBuffer.readVarInt();
        z = packetBuffer.readVarInt();
        radius = packetBuffer.readVarInt();
        biomeId = packetBuffer.readVarInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        packetBuffer.writeVarInt(x);
        packetBuffer.writeVarInt(z);
        packetBuffer.writeVarInt(radius);
        packetBuffer.writeVarInt(biomeId);
    }

    public static class Handler implements IMessageHandler<BiomeAreaChangeMessage, IMessage> {
        @Override
        public IMessage onMessage(BiomeAreaChangeMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                WorldClient world = Minecraft.getMinecraft().world;
                int x = message.x;
                int z = message.z;
                int rad = message.radius;
                for(int ix = x - rad; ix <= x + rad; ++ix) {
                    for(int iz = z - rad; iz <= z + rad; ++iz) {
                        Chunk chunk = world.getChunk(new BlockPos(ix, 0, iz));
                        int[] biomeArray = ((INewChunk) chunk).getIntBiomeArray();
                        int biomeIdAtPos = biomeArray[(iz & 0xF) << 4 | (ix & 0xF)];
                        if (biomeIdAtPos != message.biomeId) {
                            biomeArray[(iz & 0xF) << 4 | (ix & 0xF)] = message.biomeId;
                        }
                    }
                }
                world.markBlockRangeForRenderUpdate(new BlockPos(x - rad, 0, z - rad), new BlockPos(x + rad, world.getHeight(), z + rad));
            });
            return null;
        }
    }
}
