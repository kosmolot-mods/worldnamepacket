package pl.kosma.worldnamepacket.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import pl.kosma.worldnamepacket.FabricMod;


@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(at = @At("HEAD"), method = "sendWorldInfo(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/server/world/ServerWorld;)V")
	public void onSendWorldInfo(ServerPlayerEntity player, ServerWorld world, CallbackInfo info) {
		FabricMod.onServerWorldInfo(player);
	}
}
