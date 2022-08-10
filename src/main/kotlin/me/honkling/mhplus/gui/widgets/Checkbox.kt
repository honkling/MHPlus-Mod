package me.honkling.mhplus.gui.widgets

import me.honkling.mhplus.MHPlus
import me.honkling.mhplus.lib.Dim2D
import me.honkling.mhplus.lib.Settings
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.util.math.MatrixStack
import java.lang.reflect.Field

class Checkbox : DrawableHelper, Drawable, Element, Selectable {
    private var field: Field
    private var x: Int
    private var y: Int
    private var dim: Dim2D
    private val settings: Settings = MHPlus.instance.settings

    constructor(field: Field, x: Int, y: Int) {
        this.field = field
        this.x = x
        this.y = y
        dim = Dim2D(x, y, x + 8, y + 8)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val x2 = x + 8
        val y2 = y + 8
        if (field.getBoolean(settings)) {
            drawRect(matrices, x, y, x2, y2, 0xff71eec2)
            fill(matrices, x + 2, y + 2, x2 - 1, y2 - 1, 0xff71eec2.toInt())
        } else {
            drawRect(matrices, x, y, x2, y2, 0xffffffff)
        }
    }

    fun drawRect(matrices: MatrixStack, x1: Int, y1: Int, x2: Int, y2: Int, color: Long) {
        drawHorizontalLine(matrices, x1, x2, y1, color.toInt())
        drawHorizontalLine(matrices, x1, x2, y2, color.toInt())
        drawVerticalLine(matrices, x1, y1, y2, color.toInt())
        drawVerticalLine(matrices, x2, y1, y2, color.toInt())
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (dim.isWithinBounds(mouseX, mouseY)) {
            val value = field.getBoolean(settings)
            field.setBoolean(settings, !value)
            return true
        }
        return false
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
        // TODO: Not yet implemented
    }

    override fun getType(): Selectable.SelectionType {
        // TODO: Not yet implemented
        return Selectable.SelectionType.NONE
    }
}