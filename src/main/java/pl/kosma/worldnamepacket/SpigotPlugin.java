package pl.kosma.worldnamepacket;

import com.google.common.io.ByteStreams;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

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

	private Boolean usingBungee;
	private String bungeeServerName;

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_VOXELMAP, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_VOXELMAP);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_XAEROMAP);

		this.usingBungee = this.getServer().spigot().getConfig().getBoolean("settings.bungeecord", false);
		if (this.usingBungee) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_BUNGEECORD);
			this.getServer().getMessenger().registerIncomingPluginChannel(this, WorldNamePacket.CHANNEL_NAME_BUNGEECORD, this);
			this.getLogger().info("BungeeCord mode enabled, prefixing world-names with server-name");
		}
	}

	@Override
	public void onDisable() {
		this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
		this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	}

	private void sendWorldName(Player player, String channel) {
		String worldNameString;

		if (this.usingBungee) {
			String tmpName = (this.bungeeServerName != null) ? this.bungeeServerName : "UNKNOWN";
			worldNameString = tmpName + "_" + player.getWorld().getName();
		} else {
			worldNameString = player.getWorld().getName();
		}

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
		} else if (channel.contentEquals(WorldNamePacket.CHANNEL_NAME_BUNGEECORD)) {
			ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
			String subchannel = input.readUTF();

			if (subchannel.contentEquals("GetServer")) {
				this.bungeeServerName = input.readUTF();
				// update now that we know our server-name
				for (Player p : this.getServer().getOnlinePlayers()) {
					sendWorldName(p, WorldNamePacket.CHANNEL_NAME_XAEROMAP);
				}
			}
		}
	}

	/**
	 * Xaero's Map handler (sent on every world/level change).
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// if we dont know our bungee server-name, ask for it
		if (this.usingBungee && this.bungeeServerName == null) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer");
			this.getServer().sendPluginMessage(this, WorldNamePacket.CHANNEL_NAME_BUNGEECORD, out.toByteArray());
		}

		// Typical issue: the event is fired a bit too early and Xaero Map doesn't catch
		// it.
		// To make things simple, just delay sending until the next tick. It works.
		// Hopefully.
		Bukkit.getScheduler().runTask(this, () -> {
			sendWorldName(event.getPlayer(), WorldNamePacket.CHANNEL_NAME_XAEROMAP);
		});
		// re-send after ~5seconds to try mitigate slow loading
		Bukkit.getScheduler().runTaskLater(this, () -> {
			sendWorldName(event.getPlayer(), WorldNamePacket.CHANNEL_NAME_XAEROMAP);
		}, 100L);
	}

	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		sendWorldName(event.getPlayer(), WorldNamePacket.CHANNEL_NAME_XAEROMAP);
	}
}