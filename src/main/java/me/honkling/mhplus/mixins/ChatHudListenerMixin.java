package me.honkling.mhplus.mixins;

import me.honkling.mhplus.util.SettingsManager;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
	@Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
	public void onChatMessage(MessageType messageType, Text message, UUID sender, CallbackInfo ci) {
		if(SettingsManager.Instance.data.getBlockedUsers().contains(sender.toString()))
			ci.cancel();

	}
}
