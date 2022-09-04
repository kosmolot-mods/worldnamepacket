# WorldNamePacket

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Y8Y726QMH)

Server-side companion for mapping mods. Automatically sets world name
in Multiworld mode - no more manual configuration and "world not recognized"
messages.

Supported platforms: Fabric, Spigot.

Supported mapping mods: VoxelMap, Xaero's Map, JourneyMap, Rei's Minimap.

## Functionality

This mod helps in a specific situation: where you connect to a server
that has more than the 3 vanilla dimensions, mapping mods easily get confused
and can either mix up the maps or ask you which one you're on. This can
happen on many modded servers - but also vanilla servers if there are behind
a proxy (Bungeecord/Velocity/etc).

This tool solves this problem by telling the mapping mod which world it's connected to.

## Installation

Download a jar from the Releases section and drop it in your mods/plugin folder on the
**server side**. If you're using a proxy (Bungecord/Velocity/etc.) make sure to have
it installed on all servers.

The jar works on both Fabric and Spigot - cursed, I know.

## Configuration

There's nothing to configure. The mod will automatically read the world name from
your server configuration (`level-name` on vanilla, dimension name on modded).
One common issue is that all your worlds are named `world` - you'll have to fix that
for the mod to operate correctly.

## Where's the Forge version?

I don't know Forge, sorry. If you can code it up, please let me know!
