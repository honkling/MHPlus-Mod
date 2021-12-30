package me.honkling.minehutplus.mixins;

import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ResourcePackDownloadMixin {
	@ModifyVariable(method = "download", at = @At("HEAD"), ordinal = 0)
	public String url(String url) {
		return "http://0.0.0.0/";
	}
}
