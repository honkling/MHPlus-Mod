package me.honkling.mhplus.ktmixins

import me.honkling.mhplus.MHPlus
import net.minecraft.client.network.ServerAddress

class KTConnectScreenMixin {
    companion object {
        private val MINEHUT_REGEX = Regex("(.*\\.)?minehut\\.(com|gg)\\.?")
        private val LOBBY_REGEX = Regex("(.*\\.)?minehut\\.com\\.?")

        @JvmStatic
        fun connect(address: ServerAddress) {
            MHPlus.instance.isOnMinehut = address.address.matches(MINEHUT_REGEX)
            MHPlus.instance.isInLobby = address.address.matches(LOBBY_REGEX)
        }
    }
}