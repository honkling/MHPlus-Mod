@file:Suppress("Unused", "Unused_Parameter")

package me.honkling.mhplus.lib

import me.honkling.mhplus.MHPlus
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.MappingResolver
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.lang.reflect.Method

val resolver: MappingResolver = FabricLoader.getInstance().mappingResolver
val version: List<Int> = MinecraftClient.getInstance().gameVersion.split(".").map { it.toInt() }
val lowest: Int = 17

fun sendChatMessage(message: String) {
    val player: ClientPlayerEntity = MinecraftClient.getInstance().player!!

    fun sendChatMessage19() {
        val method: Method = ClientPlayerEntity::class.java.methods.find { it.name == "method_44096" }!!
        method.invoke(player, message, null)
    }

    fun sendChatMessageX() {
        val method: Method = ClientPlayerEntity::class.java.methods.find { it.name == "method_3142" }!!
        method.invoke(player, message)
    }

    return versionTernary(::sendChatMessageX, ::sendChatMessageX, ::sendChatMessage19)
}

fun setScreenAndRender(screen: Screen) {
    val client: MinecraftClient = MinecraftClient.getInstance()

    fun setScreenAndRender19() {
        val method: Method = MinecraftClient::class.java.methods.find { it.name == "method_29970" }!!
        method.invoke(client, screen)
    }

    fun setScreenAndRender18() {
        val method: Method = MinecraftClient::class.java.methods.find { it.name == "method_29970" }!!
        method.invoke(client, screen)
    }

    fun setScreenAndRender17() {
        val method: Method = MinecraftClient::class.java.methods.find { it.name == "method_1507" }!!
        method.invoke(client, screen)
    }

    return versionTernary(::setScreenAndRender17, ::setScreenAndRender18, ::setScreenAndRender19)
}

fun translatable(key: String): MutableText {
    fun translatable19(): MutableText {
        val method: Method = Text::class.java.methods.find { it.name == "method_43471" }!!
        return method.invoke(null, key) as MutableText
    }

    @Suppress("UNCHECKED_CAST")
    fun translatableX(): MutableText {
        val clazz: Class<Text> = Class.forName(resolver.mapClassName("intermediary", "net.minecraft.class_2588")) as Class<Text>
        return clazz.constructors[0].newInstance(key) as MutableText
    }

    return versionTernary(::translatableX, ::translatableX, ::translatable19)
}

@Suppress("UNCHECKED_CAST")
internal fun <T> versionTernary(vararg methods: () -> T): T {
    methods.forEachIndexed { k, v ->
        if (lowest + k == version[1]) return v.invoke()
    }

    MHPlus.instance.logger.warn("MH+ couldn't find a good candidate for a method.")
    return methods[0].invoke()
}