package me.honkling.mhplus.ktmixins

import me.honkling.mhplus.MHPlus
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class KTChatHudMixin {
    companion object {
        private val REPLACE_REGEX = Regex("([§�].)")
        private val AD_REGEX = Regex("^\\[(ad|shop)] (\\[.+] )?.+: /join .+")
        private val NPC_REGEX = Regex("^\\[npc] .+")
        private val MINEHUT_REGEX = Regex("^\\[(minehut|market|update)] .+")

        @JvmStatic
        fun addMessage(message: Text, ci: CallbackInfo) {
            val instance: MHPlus = MHPlus.instance
            if (!instance.isInLobby) return

            val msg: String = message.string.replace(REPLACE_REGEX, "").trim().lowercase()
            if (
                (instance.settings.hideAdvertisements && msg.matches(AD_REGEX)) ||
                (instance.settings.hideNpcMessages && msg.matches(NPC_REGEX)) ||
                (instance.settings.hideMinehutBroadcasts && msg.matches(MINEHUT_REGEX))
            ) ci.cancel()
        }
    }
}
