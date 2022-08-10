package me.honkling.mhplus.ktmixins

import me.honkling.mhplus.MHPlus

class KTMinecraftClientMixin {
    companion object {
        @JvmStatic
        fun close() {
            MHPlus.instance.settings.write()
        }
    }
}