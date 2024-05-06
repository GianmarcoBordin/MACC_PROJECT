package com.mygdx.game.screen.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.Constants.FIRST_HEALTH_STARTING_X
import com.mygdx.game.Constants.HEALTH_HEIGHT
import com.mygdx.game.Constants.HEALTH_STARTING_Y
import com.mygdx.game.Constants.HEALTH_WIDTH
import com.mygdx.game.Constants.PLAYER_LIFE

class HealthBar(private val texture: TextureRegion, private val startingX: Float) {

    private val healthWidthStep = HEALTH_WIDTH / PLAYER_LIFE

    fun draw(batch: SpriteBatch, life: Int) {
        batch.setColor(getHealthColor(life))
        batch.draw(texture, startingX, HEALTH_STARTING_Y, calculateWidth(life), HEALTH_HEIGHT)
        batch.setColor(Color.WHITE) // reset the draw color
    }

    private fun getHealthColor(life: Int): Color {
        return when {
            life >= 4 -> Color.GREEN
            life >= 3 -> Color.ORANGE
            else -> Color.RED
        }
    }

    private fun calculateWidth(life: Int): Float {
        return healthWidthStep * life
    }
}

