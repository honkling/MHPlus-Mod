package me.honkling.mhplus.ktmixins

import me.honkling.mhplus.MHPlus
import me.honkling.mhplus.gui.toasts.TypoToast
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.Toast
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class KTClientPlayerEntityMixin {
    companion object {
        private val TYPO_REGEX = Regex("^(.+/)?(ad|join) (?!([<\\[(]))(?!server|name).+(?!([>\\])]))")
        var lastSentMessage: String? = null
        var excuseMessage: Boolean = false

        @JvmStatic
        fun sendChatMessage(message: String, ci: CallbackInfo) {
            if (!MHPlus.instance.isOnMinehut || !message.matches(TYPO_REGEX) || excuseMessage) {
                excuseMessage = false
                return
            }

            ci.cancel()
            lastSentMessage = message
            excuseMessage = false

            if (MHPlus.instance.toast != null) MHPlus.instance.toast!!.visibility = Toast.Visibility.HIDE
            val toast = TypoToast()
            MinecraftClient.getInstance().toastManager.add(toast)
            MHPlus.instance.toast = toast
        }
    }
}