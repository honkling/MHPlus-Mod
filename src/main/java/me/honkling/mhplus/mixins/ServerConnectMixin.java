package me.honkling.mhplus.mixins;

import me.honkling.mhplus.MHPlusClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ServerConnectMixin {
	@Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V")
	private void connect(MinecraftClient client, ServerAddress address, CallbackInfo ci) {
		MHPlusClient.isOnMinehut = address.getAddress().matches("(.+\\.)?minehut\\.(com|gg)\\.?");
		MHPlusClient.isInLobby = address.getAddress().matches("(.+\\.)?minehut\\.com\\.?");
	}
}
