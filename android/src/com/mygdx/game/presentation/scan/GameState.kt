package com.mygdx.game.presentation.scan

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Line
import kotlin.math.sqrt

data class GameState(
    var gameItem: GameItem = GameItem(), // default
    // position of the top-left, not the center
    var position: Vector2 = Vector2(100.0f, 100.0f), // default
    var hitbox: MutableList<Vector2> = mutableListOf(), // default
    // current hp of the item
    var hp: Int = -1, // default
    var shootBitmap: Bitmap? = null, // default
    var shootPosition: Vector2 = Vector2(0.0f, 0.0f), // default
    var shoot: Boolean = false, // default
    var shootHitbox: MutableList<Vector2> = mutableListOf(), // default
    var isStarted: Boolean = false, // default
    var isGameOver: Boolean = false,
    var owned: Boolean = false, // default
    var lines: MutableList<Line> = mutableListOf() // default
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