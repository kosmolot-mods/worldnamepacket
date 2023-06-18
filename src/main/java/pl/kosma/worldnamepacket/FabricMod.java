package pl.kosma.worldnamepacket;

import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;


public class FabricMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
    
    @Override
    public void onInitialize() {
    	ServerPlayNetworking.registerGlobalReceiver(new Identifier(WorldNamePacket.CHANNEL_NAME_VOXELMAP),
				(server, player, handler, buf, responseSender) -> { sendResponse(player, WorldNamePacket.CHANNEL_NAME_VOXELMAP, buf); });
    }

    /**
     * Xaero's Map requires the world name to be send unprompted upon world join/change.
     */
    static public void onServerWorldInfo(ServerPlayerEntity player)
    {
		sendResponse(player, WorldNamePacket.CHANNEL_NAME_XAEROMAP, null);
    }

    static private void sendResponse(ServerPlayerEntity player, String channel, @Nullable PacketByteBuf buf)
    {
		String levelName = ((MinecraftDedicatedServer) player.getServerWorld().getServer()).getLevelName();
        byte[] requestBytes = (buf != null) ? buf.slice().array() : new byte[0];
        byte[] responseBytes = WorldNamePacket.formatResponsePacket(requestBytes, levelName);

		PacketByteBuf responsePacket = PacketByteBufs.create();
		responsePacket.writeBytes(responseBytes);

        FabricMod.LOGGER.debug("request: " + WorldNamePacket.byteArrayToHexString(requestBytes));
        FabricMod.LOGGER.debug("response: " + WorldNamePacket.byteArrayToHexString(responseBytes));
		FabricMod.LOGGER.info("WorldNamePacket: ["+channel+"] sending levelName: " + levelName);
		ServerPlayNetworking.send(player, new Identifier(channel), responsePacket);
    }
}
