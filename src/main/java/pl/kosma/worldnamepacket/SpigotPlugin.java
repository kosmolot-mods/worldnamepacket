package pl.kosma.worldnamepacket;

import java.nio.ByteBuffer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SpigotPlugin extends JavaPlugin implements PluginMessageListener {
	static byte packetID = 0;
	static String CHANNEL_NAME = "worldinfo:world_id";

	@Override
	public void onEnable() {
		this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);
	}

	@Override
	public void onDisable() {
		this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
		this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		if (channel.contentEquals(CHANNEL_NAME)) {
			byte[] world_name = player.getWorld().getName().getBytes();
			ByteBuffer buffer = ByteBuffer.allocate(2+world_name.length).put(packetID).put((byte) world_name.length).put(world_name);
			player.sendPluginMessage(this, CHANNEL_NAME, buffer.array());
		}
	}
}