package pl.kosma.worldnamepacket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import io.netty.buffer.Unpooled;

public class WorldNamePacket implements ModInitializer {
    static byte PACKET_ID = 0;
    public static final Identifier CHANNEL_NAME = new Identifier("worldinfo", "world_id");

    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(CHANNEL_NAME, (packetContext, attachedData) -> {
        	ServerWorld serverWorld = ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld();
        	MinecraftDedicatedServer dedicatedServer = (MinecraftDedicatedServer) serverWorld.getServer(); 
        	String levelName = dedicatedServer.getLevelName();   
        	System.out.println("levelName: "+levelName);
        	
        	PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        	passedData.writeByte(PACKET_ID);
        	passedData.writeByteArray(levelName.getBytes());
        	ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), CHANNEL_NAME, passedData);
        });
    }
}