# WorldNamePacket

Fabric server-side companion mod for VoxelMap. Automatically sets world name
in Multiworld mode - no more manual configuration and "world not recognized"
messages.

## Functionality

This mod helps in a specific situation: where you connect to a server
that has more than the 3 vanilla dimensions, VoxelMap easily gets confused
and can either mix up the maps or ask you which one you're on. This can
happen on many modded servers - but also vanilla servers if there are behind
a proxy (Bungeecord/Velocity/etc).

This mod solves this problem by telling VoxelMap what server it's connected to.

Under the hood: whenever the mod receives a message on the `worldinfo:world_id`
channel, it replies with the name of the current world on the same channel.

## Installation

Drop the mod in your mods folder on the **server side**. If you're using a proxy
(Bungecord/Velocity/etc.) make sure to have it installed on all servers. There are
both Spigot and Fabric versions available:

* [Download Fabric mod](https://www.curseforge.com/minecraft/mc-mods/worldnamepacket-fabric).
* [Download Spigot plugin](https://www.spigotmc.org/resources/worldnamepacket.83572/)

## Configuration

There's nothing to configure. The mod will automatically read the world name from
your server configuration (`level-name` on vanilla, dimension name on modded).
One common issue is that all your worlds are named `world` - you'll have to fix that
for the mod to operate correctly.

## Where's the Forge version?

I don't know Forge, sorry. If you can code it up, please let me know!
