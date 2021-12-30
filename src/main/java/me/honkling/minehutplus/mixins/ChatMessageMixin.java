package me.honkling.minehutplus.mixins;

import me.honkling.minehutplus.client.MinehutPlusClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatMessageMixin {
	@Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;)V", cancellable = true)
	public void addMessage(Text message, CallbackInfo ci) {
		if(MinehutPlusClient.isOnMinehut) {
			String msg = message.getString().replaceAll("((§|�).)", "");
			if(MinehutPlusClient.settings.hideAdvertisements() && msg.matches("\\[AD] (\\[.+] )?.+: /join .+")) ci.cancel();
			if(MinehutPlusClient.settings.hideNpcMessages() && msg.matches("\\[NPC] .+")) ci.cancel();
			if(MinehutPlusClient.settings.hideMarketAdvertisements() && msg.matches("\\[SHOP] .+")) ci.cancel();
		}
	}
}
