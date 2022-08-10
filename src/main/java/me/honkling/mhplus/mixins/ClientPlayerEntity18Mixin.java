package me.honkling.mhplus.mixins;

import me.honkling.mhplus.ktmixins.KTClientPlayerEntityMixin;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntity18Mixin {

    @Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;)V", cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        KTClientPlayerEntityMixin.sendChatMessage(message, ci);
    }
}
