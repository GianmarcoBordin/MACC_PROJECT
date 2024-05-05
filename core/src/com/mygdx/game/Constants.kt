package com.mygdx.game

import com.badlogic.gdx.math.Vector2

object Constants{


    // This is the player speed when the user hit the button
    const val PLAYER_SPEED = 60f

    // Size of the world
    const val WORLD_WIDTH = 128f
    const val WORLD_HEIGHT = 72f

    // Player up limit
    const val UP_LIMIT = WORLD_HEIGHT * 5/ 8

    // Starting player position
    val PLAYER_1_POSITION = Vector2(0.25f * WORLD_WIDTH,0.5f * WORLD_HEIGHT)
    val PLAYER_2_POSITION = Vector2(0.75f * WORLD_WIDTH,0.5f * WORLD_HEIGHT)
    const val PLAYER_LIFE = 5
    const val PLAYER_BOX_WIDTH = Constants.WORLD_WIDTH / 15
    const val PLAYER_BOX_HEIGHT = Constants.WORLD_WIDTH / 15

    // Laser details
    const val LASER_SPEED = 90f
    const val LASER_WIDTH = 2f
    const val LASER_HEIGHT = 0.5f

    // health bar information
    const val FIRST_HEALTH_STARTING_X = WORLD_WIDTH/8
    const val SECOND_HEALTH_STARTING_X = WORLD_WIDTH * 5/8
    const val HEALTH_STARTING_Y = WORLD_HEIGHT * 13 / 16f
    const val HEALTH_WIDTH = WORLD_WIDTH/4
    const val HEALTH_HEIGHT = WORLD_HEIGHT/ 50f

}