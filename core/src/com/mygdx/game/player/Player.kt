package com.mygdx.game.player

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.Constants
import java.util.LinkedList


data class Player(
    var origin: Vector2,

    // it's the frequency of shoots
    var timeBetweenShots: Float,

    // graphics
    var playerTextureRegion: TextureRegion,
    var laserTextureRegion: TextureRegion,
    var playerPosition: PlayerPosition,
    var life: Int,
    var damage: Int
    )
{
    interface PlayerEventListener {
        fun onGameOver(position: PlayerPosition)
    }



    // keep track of laser generate by users
    var laserList: LinkedList<Laser> = LinkedList()

    // collision detection
    private var playerBox: Rectangle = Rectangle(origin.x, origin.y, Constants.WORLD_WIDTH / 15, Constants.WORLD_WIDTH / 15)

    // position & dimension
    private var timeSinceLastShot = 1f

    private var playerEventListener: PlayerEventListener? = null

    fun setPlayerEventListener(listener: PlayerEventListener) {
        playerEventListener = listener
    }


    fun xPosition(): Float{
        return playerBox.x
    }

    fun yPosition(): Float{
        return playerBox.y
    }


    fun update(deltaTime: Float) {
        timeSinceLastShot += deltaTime
    }

    fun canFireLaser(): Boolean {
        return timeSinceLastShot - timeBetweenShots >= 0
    }


    fun translate(xChange: Float, yChange: Float) {
        playerBox.setPosition(playerBox.x + xChange, playerBox.y + yChange)
    }

    fun draw(batch: Batch) {
        batch.draw(playerTextureRegion, playerBox.x, playerBox.y, playerBox.width, playerBox.height)

    }

    // used for collision detection
    fun intersects(rectangle: Rectangle): Boolean{
        return playerBox.overlaps(rectangle)
    }


    fun fireLasers(playerPosition: PlayerPosition): Laser {
        val xStart = if (playerPosition == PlayerPosition.RIGHT) playerBox.x else playerBox.x + playerBox.width

        val laser = Laser(
            xStart,
            playerBox.y + playerBox.height / 2,
            Constants.LASER_WIDTH,
            Constants.LASER_HEIGHT,
            Constants.LASER_SPEED,
            laserTextureRegion
        )
        timeSinceLastShot = 0f
        return laser
    }

    // return true only when you loose
    fun hit(damage: Int){
        life -= damage
        if (life <= 0) {
            playerEventListener?.onGameOver(playerPosition)
        }

    }

    fun move(newX:Float, newY:Float){
        playerBox.x = newX
        playerBox.y = newY
    }

}