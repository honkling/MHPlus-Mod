package me.honkling.mhplus.gui.screens

import me.honkling.mhplus.gui.widgets.Checkbox
import me.honkling.mhplus.lib.Settings
import me.honkling.mhplus.lib.translatable
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class SettingsScreen : Screen(translatable("settings.screen.title")) {
    private val fields = Settings::class.java.declaredFields

    override fun init() {
        for (i in fields.indices) {
            val field = fields[i]
            if (field.type != Boolean::class.java) continue
            field.isAccessible = true // the field is public. i don't know why i'm required to do this.
            addDrawableChild(Checkbox(field, width / 2 - 12, i * 12 + 8))
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        fill(
            matrices,
            4,
            4,
            width / 2 + 4,
            height / 2 + 4,
            0xc8000000.toInt()
        )

        super.render(matrices, mouseX, mouseY, delta)

        DrawableHelper.drawTextWithShadow(
            matrices,
            MinecraftClient.getInstance().textRenderer,
            title,
            7,
            8,
            0xffffffff.toInt()
        )

        matrices.scale(0.85f, 0.85f, 1f)

        for (i in fields.indices) {
            val field = fields[i]
            if (field.type != Boolean::class.java) continue
            DrawableHelper.drawTextWithShadow(
                matrices,
                MinecraftClient.getInstance().textRenderer,
                translatable("settings.${field.name}.name"),
                8,
                i * 14 + 10,
                0xffAAAAAA.toInt()
            )
        }
    }
}