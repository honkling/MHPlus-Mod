package me.honkling.mhplus.lib

class Dim2D {
    private var x1: Int
    private var y1: Int
    private var x2: Int
    private var y2: Int

    constructor(x1: Int, y1: Int, x2: Int, y2: Int) {
        this.x1 = x1
        this.y1 = y1
        this.x2 = x2
        this.y2 = y2
    }

    fun isWithinBounds(mouseX: Double, mouseY: Double): Boolean {
        return x1 <= mouseX && x2 >= mouseX && y1 <= mouseY && y2 >= mouseY
    }
}