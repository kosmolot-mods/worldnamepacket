package pl.kosma.worldnamepacket;

import java.nio.ByteBuffer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SpigotPlugin extends JavaPlugin implements Listener, PluginMessageListener {
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_VOXELMAP, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_VOXELMAP);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_XAEROMAP);
	}

	@Override
	public void onDisable() {
		this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
		this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	}
	
	private void sendWorldName(Player player, String channel) {
		String worldNameString = player.getWorld().getName();
		byte[] worldNameBytes = worldNameString.getBytes();
		this.getLogger().info("WorldNamePacket: ["+channel+"] sending levelName: " + worldNameString);
		ByteBuffer buffer = ByteBuffer.allocate(2+worldNameBytes.length)
				                      .put(WorldNamePacket.PACKET_ID)
				                      .put((byte) worldNameBytes.length)
				                      .put(worldNameBytes);
		player.sendPluginMessage(this, channel, buffer.array());
	}

	/**
	 * VoxelMap (and other map plugins) handler (request-response).
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		if (channel.contentEquals(WorldNamePacket.CHANNEL_NAME_VOXELMAP)) {
			sendWorldName(player, channel);
		}
	}
	
	/**
	 * Xaero's Map handler (sent on every world/level change). 
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Typical issue: the event is fired a bit too early and Xaero Map doesn't catch it.
		// To make things simple, just delay sending until the next tick. It works. Hopefully.
		Bukkit.getScheduler().runTask(this, () -> {
			sendWorldName(event.getPlayer(), WorldNamePacket.CHANNEL_NAME_XAEROMAP);
		});
	}

	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		sendWorldName(event.getPlayer(), WorldNamePacket.CHANNEL_NAME_XAEROMAP);
	}
}