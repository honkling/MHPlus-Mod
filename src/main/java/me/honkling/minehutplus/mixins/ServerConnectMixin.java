package me.honkling.minehutplus.mixins;

import me.honkling.minehutplus.client.MinehutPlusClient;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;

@Mixin(ClientConnection.class)
public class ServerConnectMixin {
	@Inject(at = @At("HEAD"), method = "connect")
	private static void onConnect(InetSocketAddress address, boolean useEpoll, CallbackInfoReturnable<ClientConnection> cir) {
		MinehutPlusClient.isOnMinehut = address.getHostName().matches("(.+\\.)?minehut\\.(com|gg)\\.?");
	}
}
