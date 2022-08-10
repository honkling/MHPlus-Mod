package me.honkling.mhplus

import me.honkling.mhplus.gui.screens.SettingsScreen
import me.honkling.mhplus.gui.toasts.TypoToast
import me.honkling.mhplus.ktmixins.KTClientPlayerEntityMixin
import me.honkling.mhplus.lib.Settings
import me.honkling.mhplus.lib.sendChatMessage
import me.honkling.mhplus.lib.setScreenAndRender
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.toast.Toast
import net.minecraft.client.util.InputUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
class MHPlus : ClientModInitializer {
    companion object {
        lateinit var instance: MHPlus
    }
    val logger: Logger = LogManager.getLogger("MHPlus")
    private val settingsKey = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
        "settings.key.name",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_K,
        "settings.category.name",
    )
    )
    lateinit var settings: Settings
    var isOnMinehut: Boolean = false
    var isInLobby: Boolean = false
    var toast: TypoToast? = null

    override fun onInitializeClient() {
        settings = Settings()
        instance = this

        // Check if keybinds are pressed
        ClientTickEvents.END_CLIENT_TICK.register {
            if (toast != null) {
                if (toast!!.progress <= 1) toast!!.progress += 0.01
                else toast!!.visibility = Toast.Visibility.HIDE
            }

            while (settingsKey.wasPressed()) {
                if (toast != null && toast!!.progress <= 1) {
                    KTClientPlayerEntityMixin.excuseMessage = true
                    sendChatMessage(KTClientPlayerEntityMixin.lastSentMessage!!)
                    toast!!.visibility = Toast.Visibility.HIDE
                    return@register
                }
                setScreenAndRender(SettingsScreen())
            }
        }

        logger.info("Initialized.")
    }
}