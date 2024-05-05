package com.mygdx.game.presentation.scan

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Line
import kotlin.math.sqrt

data class GameState(
    var gameItem: GameItem,
    // position of the top-left, not the center
    var position: Vector2 = Vector2(100.0f, 100.0f),
    var hitbox: MutableList<Vector2> = mutableListOf(),
    // current hp of the item
    var hp: Int,
    var shootBitmap: Bitmap? = null,
    var shootPosition: Vector2 = Vector2(0.0f, 0.0f),
    var shoot: Boolean = false,
    var shootHitbox: MutableList<Vector2> = mutableListOf(),
    var isStarted: Boolean = false,
    var isGameOver: Boolean = false,
    var owned: Boolean = false,
    var lines: MutableList<Line> = mutableListOf()
)

data class Vector2 (
    var x: Float,
    var y: Float
) {
    // Method to add another vector
    fun add(other: Vector2): Vector2 {
        return Vector2(x + other.x, y + other.y)
    }

    // Method to subtract another vector
    fun subtract(other: Vector2): Vector2 {
        return Vector2(x - other.x, y - other.y)
    }

    // Method to multiply by a scalar
    fun multiply(scalar: Float): Vector2 {
        return Vector2(x * scalar, y * scalar)
    }

    // Method to calculate the magnitude (length) of the vector
    private fun magnitude(): Float {
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    // Method to normalize the vector (make its length 1)
    fun normalize(): Vector2 {
        val mag = magnitude()
        return if (mag != 0.0f) Vector2((x / mag), (y / mag)) else Vector2(0.0f, 0.0f)
    }

    fun update(newPosition: Vector2) {
        x = newPosition.x
        y = newPosition.y
    }

    fun toOffset() : Offset {
        return Offset(x, y)
    }
}