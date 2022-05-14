package me.honkling.mhplus.mixins;

import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	@Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
	public void sendChatMessage(String message, CallbackInfo ci) {
		if (SettingsManager.Instance.settings.isProtectAdvertisementTypos() && message.matches("^(.+\\/)?(ad|join) (?!(<|\\[|\\())(?!server|name).+(?!(>|]|\\)))")) {
			if (SettingsManager.Instance.excuseMessage) {
				SettingsManager.Instance.excuseMessage = false;
				return;
			}

			ci.cancel();

			ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mhplus sendMessage");
			Style style = Style.EMPTY
					.withFormatting(Formatting.WHITE, Formatting.UNDERLINE)
					.withClickEvent(clickEvent);
			MutableText text = new LiteralText("MH+ has cancelled that message because of a potential typo that could get you muted. ")
					.formatted(Formatting.GRAY)
					.append(new LiteralText(
							"Send the message?"
					).setStyle(style));

			SettingsManager.Instance.message = message;
			MinecraftClient.getInstance().player.sendMessage(MHPlusClient.prefix(text), false);
		}
	}


}