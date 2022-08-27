package pl.kosma.worldnamepacket;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;


@Plugin(id = "worldnamepacket",
        name = "World Name Packet", version = "1.4.0",
        url = "https://www.curseforge.com/minecraft/mc-mods/worldnamepacket",
        description = "Server-side companion for mapping mods",
        authors = {"Kosmolot"})
public class VelocityPlugin {
    private final ProxyServer server;
    private final Logger logger;

    ChannelIdentifier channelIdentifierVoxelmap = MinecraftChannelIdentifier.from(WorldNamePacket.CHANNEL_NAME_VOXELMAP);
    ChannelIdentifier channelIdentifierXaeromap = MinecraftChannelIdentifier.from(WorldNamePacket.CHANNEL_NAME_XAEROMAP);

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.server.getChannelRegistrar().register(channelIdentifierVoxelmap);
        this.server.getChannelRegistrar().register(channelIdentifierXaeromap);
    }

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if (event.getIdentifier() == channelIdentifierVoxelmap && event.getSource() instanceof Player) {
            sendWorldName((Player) event.getSource(), event.getIdentifier(), event.getData());
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        sendWorldName(event.getPlayer(), channelIdentifierXaeromap, null);
    }

    private void sendWorldName(Player player, ChannelIdentifier channel, byte[] bytes) {
        ServerConnection serverConnection = player.getCurrentServer().orElse(null);
        if (serverConnection == null)
            return;
        String worldName = serverConnection.getServer().getServerInfo().getName();
        byte[] responseBytes = WorldNamePacket.formatResponsePacket(bytes, worldName);
        this.logger.info("WorldNamePacket: ["+channel.getId()+"] sending worldName: " + worldName);
        player.sendPluginMessage(channel, responseBytes);
    }
}
