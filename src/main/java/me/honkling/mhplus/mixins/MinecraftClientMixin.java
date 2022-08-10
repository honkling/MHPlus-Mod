package me.honkling.mhplus.mixins;

import me.honkling.mhplus.ktmixins.KTMinecraftClientMixin;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "close")
    public void close(CallbackInfo _ci) {
        KTMinecraftClientMixin.close();
    }
}
