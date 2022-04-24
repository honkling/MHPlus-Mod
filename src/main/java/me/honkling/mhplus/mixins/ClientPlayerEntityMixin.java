package me.honkling.mhplus.mixins;

import com.google.gson.Gson;
import me.honkling.mhplus.util.SettingsManager;
import me.honkling.mhplus.util.serializables.MessageObject;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	private boolean messageConfirmed = false;

	@Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
	public void sendChatMessage(String message, CallbackInfo ci) {
		if(SettingsManager.Instance.settings.isProtectAdvertisementTypos() && message.matches("^(./)?(ad|join) (?!([<\\[(]).*(server ?)?(name)?.*([>\\])]))")) {
			ci.cancel();

			Gson gson = new Gson();
			MessageObject messageObj = new MessageObject(message);
			String clickParameter = gson.toJson(messageObj);

			ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.byName("RUN_CODE"), "confirmSendMessage " + clickParameter);
			MutableText text = new LiteralText("MH+ has cancelled that message because of a potential typo that could get you muted. ")
					.formatted(Formatting.GRAY)
					.append(new LiteralText(
							"Send the message?"
					).formatted(Formatting.WHITE, Formatting.UNDERLINE));
		}
	}


}