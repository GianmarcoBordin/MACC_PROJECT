package com.mygdx.game.screen.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.mygdx.game.Constants.HEALTH_HEIGHT
import com.mygdx.game.Constants.HEALTH_STARTING_Y
import com.mygdx.game.Constants.HEALTH_WIDTH

class HealthBar(private val texture: TextureRegion, private val startingX: Float, private val playerLife: Int) {

    private val healthWidthStep = HEALTH_WIDTH / playerLife

    fun draw(batch: SpriteBatch, life: Int) {
        batch.setColor(getHealthColor(life))
        batch.draw(texture, startingX, HEALTH_STARTING_Y, calculateWidth(life), HEALTH_HEIGHT)
        batch.setColor(Color.WHITE) // reset the draw color
    }

    private fun getHealthColor(life: Int): Color {
        return when {
            life >= playerLife / 2 -> Color.GREEN
            life >= playerLife / 4 -> Color.ORANGE
            else -> Color.RED
        }
    }

    private fun calculateWidth(life: Int): Float {
        return healthWidthStep * life
    }
}

