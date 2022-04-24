package me.honkling.mhplus.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public class ParticleLimiterMixin {
	@Inject(at = @At("HEAD"), method = "onParticle", cancellable = true)
	public void addParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		if (packet.getCount() > 500)
			ci.cancel();
	}
}