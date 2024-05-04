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

    )
{

    var life = 5

    // collision detection TODO
    private var playerBox: Rectangle = Rectangle(origin.x, origin.y, Constants.WORLD_WIDTH / 15, Constants.WORLD_WIDTH / 15)

    // position & dimension
    private var timeSinceLastShot = 1f

    // keep track of laser generate by users
    var laserList: LinkedList<Laser> = LinkedList()

    fun xPosition(): Float{
        return playerBox.x
    }

    fun yPosition(): Float{
        return playerBox.y
    }

    fun width(): Float{
        return playerBox.width
    }

    fun height(): Float{
        return playerBox.height
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


    fun fireLasers(playerType: PlayerType): Laser {

        val xStart = if (playerType == PlayerType.RED) playerBox.x else playerBox.x + playerBox.width

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


    fun hit() {
        life--
        if (life < 0) {
            println("GAME OVER")
            return
        }
    }

    fun move(newX:Float, newY:Float){
        playerBox.x = newX
        playerBox.y = newY
    }

}