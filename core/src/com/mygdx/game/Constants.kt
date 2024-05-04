package com.mygdx.game

import com.badlogic.gdx.math.Vector2

object Constants{


    // This is the player speed when the user hit the button
    const val PLAYER_SPEED = 60f

    // Size of the world
    const val WORLD_WIDTH = 128f
    const val WORLD_HEIGHT = 72f

    // Starting player position
    val PLAYER_1_POSITION = Vector2(0.25f * WORLD_WIDTH,0.5f * WORLD_HEIGHT)
    val PLAYER_2_POSITION = Vector2(0.75f * WORLD_WIDTH,0.5f * WORLD_HEIGHT)

    // Laser details
    const val LASER_SPEED = 90f
    const val LASER_WIDTH = 2f
    const val LASER_HEIGHT = 0.5f
}