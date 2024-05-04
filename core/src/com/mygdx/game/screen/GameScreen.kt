package com.mygdx.game.screen


import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.mygdx.game.GameManager
import com.mygdx.game.Constants
import com.mygdx.game.Constants.PLAYER_1_POSITION
import com.mygdx.game.Constants.PLAYER_2_POSITION
import com.mygdx.game.Constants.WORLD_HEIGHT
import com.mygdx.game.Constants.WORLD_WIDTH
import com.mygdx.game.Controller
import com.mygdx.game.multiplayer.MultiplayerClient
import com.mygdx.game.player.SoundManager
import com.mygdx.game.player.Laser
import com.mygdx.game.player.Player
import com.mygdx.game.player.PlayerType

// TODO use the GameManager to change screen when something happen
class GameScreen(
    private val game: GameManager,
    private var multiplayerClient: MultiplayerClient,
    private var playerType: PlayerType) : ScreenAdapter(), MultiplayerClient.GameEventListener
{

    private var batch: SpriteBatch? = null
    private var camera: OrthographicCamera? = null

    private var textureAtlas: TextureAtlas
    private var controller: Controller

    private var viewport: ExtendViewport
    private var background : TextureRegion

    // player info
    private var firstPlayer: Player
    private var secondPlayer: Player
    private var soundManager: SoundManager

    private val firstPlayerLaserDirection = if (playerType == PlayerType.RED) -1f else 1f
    private val secondPlayerLaserDirection = -1 * firstPlayerLaserDirection

    // TODO
    private val healthTexture = Texture("images/blank.png")

    init {

        multiplayerClient.setGameEventListener(this)

        camera = OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT)
        camera!!.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT)
        viewport = ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
        batch = SpriteBatch()

        textureAtlas = TextureAtlas("atlas/images.atlas")

        background = textureAtlas.findRegion("Background")

        val greenPlayerXPosition = if (playerType == PlayerType.GREEN) PLAYER_1_POSITION else PLAYER_2_POSITION
        val redPlayerXPosition = if (playerType == PlayerType.GREEN) PLAYER_2_POSITION else PLAYER_1_POSITION

        // Create players
        firstPlayer = Player(
            greenPlayerXPosition, 0.15f,
            textureAtlas.findRegion(if (playerType == PlayerType.GREEN) "Gunner_Green_image" else "Gunner_Red_image"),
            textureAtlas.findRegion(if (playerType == PlayerType.GREEN) "laserGreen" else "laserRed")
        )

        secondPlayer = Player(
            redPlayerXPosition, 0.15f,
            textureAtlas.findRegion(if (playerType == PlayerType.GREEN) "Gunner_Red_image" else "Gunner_Green_image"),
            textureAtlas.findRegion(if (playerType == PlayerType.GREEN) "laserRed" else "laserGreen")
        )
        // sound manager
        soundManager = SoundManager()
        soundManager.playBattle()
        controller = Controller(batch!!)

    }

    override fun show() {

    }

    // Method used as a callback when the websocket client receives a movement message
    override fun onMovementMessage(newX: Float, newY: Float) {
        secondPlayer.move(newX, newY)
    }

    // Method used as a callback when the websocket client receives a new laser message
    override fun onLaserMessage(startX: Float, startY: Float) {
        // TODO I don't know if startX and startY are useful
        val laser = secondPlayer.fireLasers(PlayerType.RED)
        secondPlayer.laserList.add(laser)
        soundManager.playShoot()
    }


    override fun render(delta: Float) {
        // translate the view of the camera to the screen using projection matrix
        batch!!.projectionMatrix = camera!!.combined

        // open the sprite batch buffer for drawing
        batch!!.begin()

        // handle the input
        handleMovingInput(delta)

        firstPlayer.update(delta)
        secondPlayer.update(delta)

        // draw the background image
        batch!!.draw(background,0f,0f, WORLD_WIDTH, WORLD_HEIGHT)

        firstPlayer.draw(batch!!)
        secondPlayer.draw(batch!!)


        // create new laser if player can do it
        renderFirstPlayerLaser(delta)
        renderSecondPlayerLaser(delta)

        // collision detection
        detectPlayerCollision(firstPlayer, secondPlayer)
        detectPlayerCollision(secondPlayer, firstPlayer)

        if (firstPlayer.life >= 4)
            batch!!.setColor(Color.GREEN);
        else if (firstPlayer.life >= 3 )
            batch!!.setColor(Color.ORANGE);
        else
            batch!!.setColor(Color.RED);

        if (secondPlayer.life >= 4)
            batch!!.setColor(Color.GREEN);
        else if (secondPlayer.life >= 3 )
            batch!!.setColor(Color.ORANGE);
        else
            batch!!.setColor(Color.RED);

        batch!!.draw(healthTexture, WORLD_WIDTH/8 , WORLD_HEIGHT/10f, WORLD_WIDTH/4 * firstPlayer.life,5f)
        batch!!.draw(healthTexture, WORLD_WIDTH * 5/8 , WORLD_HEIGHT/10f, WORLD_WIDTH/4 * firstPlayer.life,5f)
        batch!!.setColor(Color.WHITE);


        batch!!.end()

        controller.draw()

    }

    override fun resume() {
        soundManager.playBattle()
    }

    override fun pause() {
        soundManager.stopBattle()
    }

    override fun hide() {
        batch!!.dispose()
        textureAtlas.dispose()
        soundManager.dispose()
        soundManager.stopBattle()

    }


    private fun handleMovingInput(deltaTime: Float) {

        if (!controller.isMoving()) return

        val playerX = firstPlayer.xPosition()
        val playerY = firstPlayer.yPosition()
        val playerWidth = firstPlayer.width()
        val playerHeight = firstPlayer.height()

        val leftLimit: Float = if (playerType == PlayerType.GREEN) -playerX else -playerX + WORLD_WIDTH / 2
        val rightLimit = if (playerType == PlayerType.GREEN) WORLD_WIDTH / 2f - playerX - playerWidth else WORLD_WIDTH - playerX - playerWidth
        val downLimit = -playerY
        val upLimit = WORLD_HEIGHT - playerY - playerHeight

        val moveSpeed = Constants.PLAYER_SPEED * deltaTime

        // Move right
        if (controller.isRightPressed && rightLimit > 0) {
            firstPlayer.translate(moveSpeed, 0f)

        }
        // Move left
        else if (controller.isLeftPressed && leftLimit < 0) {
            firstPlayer.translate(-moveSpeed, 0f)
        }
        // Move up
        else if (controller.isUpPressed && upLimit > 0) {
            firstPlayer.translate(0f, moveSpeed)
        }
        // Move down
        else if (controller.isDownPressed && downLimit < 0) {
            firstPlayer.translate(0f, -moveSpeed)
        }

        multiplayerClient.sendPlayerMovementMessage(firstPlayer.xPosition(), firstPlayer.yPosition())

    }


    private fun renderFirstPlayerLaser(deltaTime: Float){

        if (controller.isFightPressed && firstPlayer.canFireLaser()) {
            val laser = firstPlayer.fireLasers(playerType)
            firstPlayer.laserList.add(laser)
            soundManager.playShoot()

            // TODO here send laser message to other player??
            multiplayerClient.sendLaserMessage(laser.laserBox.x, laser.laserBox.y)
        }


        // draw laser and remove old ones
        val iterator: MutableListIterator<Laser> = firstPlayer.laserList.listIterator()
        while (iterator.hasNext()) {

            val laser = iterator.next()
            laser.draw(batch!!)



            val movement = firstPlayerLaserDirection * laser.movementSpeed * deltaTime

            // move laser box
            laser.laserBox.x += movement

            if (laser.laserBox.x > WORLD_WIDTH || laser.laserBox.x + laser.laserBox.width < 0){
                iterator.remove()
            }


        }
    }

    // laser rendered from the other player
    private fun renderSecondPlayerLaser(deltaTime: Float){

        // draw laser and remove old ones
        val iterator: MutableListIterator<Laser> = secondPlayer.laserList.listIterator()
        while (iterator.hasNext()) {

            val laser = iterator.next()
            laser.draw(batch!!)

            val movement = secondPlayerLaserDirection * laser.movementSpeed * deltaTime

            // move laser box
            laser.laserBox.x += movement

            if (laser.laserBox.x > WORLD_WIDTH || laser.laserBox.x + laser.laserBox.width < 0){
                iterator.remove()
            }


        }
    }




    private fun detectPlayerCollision(player: Player, otherPlayer: Player) {
        val laserIterator: MutableListIterator<Laser> = player.laserList.listIterator()

        while (laserIterator.hasNext()) {
            val laser = laserIterator.next()

            if (otherPlayer.intersects(laser.laserBox)) {
                otherPlayer.hit()
                laserIterator.remove()
                break
            }
        }
    }


}