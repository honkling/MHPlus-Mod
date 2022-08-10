package me.honkling.mhplus.gui.toasts

import com.mojang.blaze3d.systems.RenderSystem
import me.honkling.mhplus.lib.translatable
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.client.util.math.MatrixStack

class TypoToast : Toast {
    var visibility: Toast.Visibility = Toast.Visibility.SHOW
    var progress: Double = 0.0

    override fun draw(matrices: MatrixStack, manager: ToastManager, startTime: Long): Toast.Visibility {
        RenderSystem.setShaderTexture(0, Toast.TEXTURE)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        manager.drawTexture(matrices, 0, 0, 0, 64, this.width, this.height)

        val text: TextRenderer = MinecraftClient.getInstance().textRenderer
        text.draw(matrices, translatable("messages.protectedFromAdvertisement.title").string, 30.0f, 7.0f, 0xFFD0D834.toInt())
        text.draw(matrices, translatable("messages.protectedFromAdvertisement.description").string, 30.0f, 18.0f, 0xFFFFFFFF.toInt())

        DrawableHelper.fill(matrices, 3, 28, 157, 29, -1)
        DrawableHelper.fill(matrices, 3, 28, (3 + progress * 154).toInt(), 29, -16755456)

        return visibility
    }
}