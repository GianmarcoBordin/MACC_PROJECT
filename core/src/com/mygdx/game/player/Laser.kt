package com.mygdx.game.player

import com.badlogic.gdx.graphics.g2d.Batch

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

class Laser(
    xStart: Float,
    yStart: Float,
    width: Float,
    height: Float,
    var movementSpeed: Float,
    private var textureRegion: TextureRegion
) {

    var laserBox: Rectangle = Rectangle(xStart, yStart, width, height)

    fun draw(batch: Batch) {
        batch.draw(textureRegion, laserBox.x, laserBox.y, laserBox.width, laserBox.height)
    }
}