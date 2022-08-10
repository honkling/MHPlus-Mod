package me.honkling.mhplus.mixins;

import me.honkling.mhplus.ktmixins.KTClientPlayerEntityMixin;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntity19Mixin {

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", cancellable = true)
    public void sendChatMessage19(String message, Text preview, CallbackInfo ci) {
        KTClientPlayerEntityMixin.sendChatMessage(message, ci);
    }
}
