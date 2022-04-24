package me.honkling.mhplus.mixins;

import me.honkling.mhplus.util.StackManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ClickEventMixin {

	@Inject(at = @At("HEAD"), method = "handleTextClick", cancellable = true)
	public void handleTextClick(Style style, CallbackInfoReturnable<Boolean> cir) {
		if (style == null || style.getClickEvent() == null)
			return;

		ClickEvent clickEvent = style.getClickEvent();
		if (clickEvent.getAction().getName().equalsIgnoreCase("run_code")) {
			String fullValue = clickEvent.getValue();
			String value = fullValue.split("\s+")[0];
			StackManager.getInstance().emit(value, clickEvent);
		}
	}
}
