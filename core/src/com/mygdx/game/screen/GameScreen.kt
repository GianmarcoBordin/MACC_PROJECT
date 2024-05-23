package com.mygdx.game.screen


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.mygdx.game.Constants
import com.mygdx.game.Constants.FIRST_HEALTH_STARTING_X
import com.mygdx.game.Constants.PLAYER_1_POSITION
import com.mygdx.game.Constants.PLAYER_2_POSITION
import com.mygdx.game.Constants.PLAYER_BOX_HEIGHT
import com.mygdx.game.Constants.PLAYER_BOX_WIDTH
import com.mygdx.game.Constants.SECOND_HEALTH_STARTING_X
import com.mygdx.game.Constants.UP_LIMIT
import com.mygdx.game.Constants.WORLD_HEIGHT
import com.mygdx.game.Constants.WORLD_WIDTH
import com.mygdx.game.screen.components.Controller
import com.mygdx.game.GameManager
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.multiplayer.MultiplayerClient
import com.mygdx.game.screen.components.HealthBar
import com.mygdx.game.player.Laser
import com.mygdx.game.player.Player
import com.mygdx.game.player.PlayerPosition
import com.mygdx.game.player.SoundManager
import com.mygdx.game.screen.components.HeadsUp



class GameScreen(
    private val game: GameManager,
    private val multiplayerClient: MultiplayerClient,
    private var playerPosition: PlayerPosition,
    myId: String,
    private val otherId: String,
    private val myPlayerType: CharacterType,
    private val otherPlayerType: CharacterType
) : ScreenAdapter(), MultiplayerClient.GameEventListener, Player.PlayerEventListener
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

    private val firstPlayerLaserDirection = if (playerPosition == PlayerPosition.RIGHT) -1f else 1f
    private val secondPlayerLaserDirection = -1 * firstPlayerLaserDirection

    private var firstPlayerHealthBar : HealthBar
    private var secondPlayerHealthBar : HealthBar
    private var headsUp: HeadsUp

    private lateinit var firstPlayerTexture: TextureRegion
    private lateinit var secondPlayerTexture: TextureRegion
    private lateinit var firstPlayerLaserTexture: TextureRegion
    private lateinit var secondPlayerLaserTexture: TextureRegion

    init {
        Gdx.app.log("GAME","MY PLAYER POSITION: $playerPosition")
        Gdx.app.log("GAME","ME: ${myPlayerType.type} ${myPlayerType.hp} ${myPlayerType.damage}")
        Gdx.app.log("GAME","OTHER: ${otherPlayerType.type} ${otherPlayerType.hp} ${otherPlayerType.damage}")

        multiplayerClient.setGameEventListener(this)

        camera = OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT)
        camera?.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT)
        viewport = ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
        batch = SpriteBatch()

        textureAtlas = TextureAtlas("atlas/images.atlas")

        background = textureAtlas.findRegion("arena-background")

        val leftPlayerXPosition = if (playerPosition == PlayerPosition.LEFT) PLAYER_1_POSITION else PLAYER_2_POSITION
        val rightPlayerXPosition = if (playerPosition == PlayerPosition.LEFT) PLAYER_2_POSITION else PLAYER_1_POSITION

        // set player texture according to the player received in input
        setPlayerTexture()


        // Create player 1
        firstPlayer = Player(
            leftPlayerXPosition, 0.15f,
            firstPlayerTexture,
            firstPlayerLaserTexture,
            playerPosition,
            myPlayerType.hp,
            myPlayerType.damage

        )

        // Create player 2
        secondPlayer = Player(
            rightPlayerXPosition, 0.15f,
            secondPlayerTexture,
            secondPlayerLaserTexture,
            playerPosition.opposite(),
            otherPlayerType.hp,
            otherPlayerType.damage
        )

        // listen only the first player, the second one sends the message through websocket
        firstPlayer.setPlayerEventListener(this)
        secondPlayer.setPlayerEventListener(this)

        // sound manager
        soundManager = SoundManager()
        soundManager.playBattle()
        controller = Controller(batch!!)
        headsUp = if (PlayerPosition.LEFT == playerPosition) HeadsUp(batch, myId, otherId) else HeadsUp(batch, otherId, myId)

        // generate health box for each user
        val healthTexture = textureAtlas.findRegion("blank")
        firstPlayerHealthBar = if (PlayerPosition.LEFT == playerPosition) HealthBar(healthTexture, FIRST_HEALTH_STARTING_X, myPlayerType.hp) else HealthBar(healthTexture, SECOND_HEALTH_STARTING_X, myPlayerType.hp)
        secondPlayerHealthBar = if (PlayerPosition.LEFT == playerPosition) HealthBar(healthTexture, SECOND_HEALTH_STARTING_X, otherPlayerType.hp) else HealthBar(healthTexture, FIRST_HEALTH_STARTING_X, otherPlayerType.hp)

    }

    private fun setPlayerTexture(){
        val myTextureName = myPlayerType.type.getSkinRegionName()
        val otherTextureName = otherPlayerType.type.getSkinRegionName()
        val secondAtlas = TextureAtlas("atlas/images.atlas")

        firstPlayerTexture = textureAtlas.findRegion(myTextureName)
        secondPlayerTexture = secondAtlas.findRegion(otherTextureName)

        firstPlayerLaserTexture = textureAtlas.findRegion(if (playerPosition == PlayerPosition.LEFT) "laserGreen" else "laserRed")
        secondPlayerLaserTexture = secondAtlas.findRegion(if (playerPosition == PlayerPosition.LEFT) "laserRed" else "laserGreen")


        if (playerPosition == PlayerPosition.RIGHT){
            Gdx.app.log("CLIENT","RIGHT")
            firstPlayerTexture.flip(true,false)

        }
        else {
            Gdx.app.log("CLIENT","LEFT")
            secondPlayerTexture.flip(true,false)

        }

    }



    override fun show() {

    }


    // Method used as a callback when the websocket client receives a movement message
    override fun onMovementMessage(newX: Float, newY: Float) {
        secondPlayer.move(newX, newY)
    }

    // Method used as a callback when the websocket client receives a new laser message
    override fun onLaserMessage() {

        val laser = secondPlayer.fireLasers(playerPosition.opposite())
        secondPlayer.laserList.add(laser)
        soundManager.playShoot()
    }



    override fun render(delta: Float) {
        // translate the view of the camera to the screen using projection matrix
        batch?.projectionMatrix = camera?.combined

        // open the sprite batch buffer for drawing
        batch?.begin()

        // handle the input
        handleMovingInput(delta)

        firstPlayer.update(delta)
        secondPlayer.update(delta)

        // draw the background image
        batch?.draw(background,0f,0f, WORLD_WIDTH, WORLD_HEIGHT)

        firstPlayer.draw(batch!!)
        secondPlayer.draw(batch!!)


        // create new laser if player can do it
        renderFirstPlayerLaser(delta)
        renderSecondPlayerLaser(delta)

        // collision detection
        detectPlayerCollision(firstPlayer, secondPlayer)
        detectPlayerCollision(secondPlayer, firstPlayer)


        firstPlayerHealthBar.draw(batch!!, firstPlayer.life)
        secondPlayerHealthBar.draw(batch!!, secondPlayer.life)

        batch?.end()

        controller.draw()
        headsUp.draw()

    }

    override fun resume() {
        soundManager.playBattle()
    }

    override fun pause() {
        soundManager.stopBattle()
    }

    override fun hide() {
        //multiplayerClient.disconnect()
        batch?.dispose()
        textureAtlas.dispose()
        soundManager.dispose()
        soundManager.stopBattle()

    }


    private fun handleMovingInput(deltaTime: Float) {

        if (!controller.isMoving()) return

        val playerX = firstPlayer.xPosition()
        val playerY = firstPlayer.yPosition()

        val leftLimit: Float = if (playerPosition == PlayerPosition.LEFT) -playerX else - playerX + WORLD_WIDTH / 2
        val rightLimit = if (playerPosition == PlayerPosition.LEFT) WORLD_WIDTH / 2f - playerX - PLAYER_BOX_WIDTH else WORLD_WIDTH - playerX - PLAYER_BOX_WIDTH
        val downLimit = -playerY
        val upLimit = UP_LIMIT - playerY - PLAYER_BOX_HEIGHT

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
            val laser = firstPlayer.fireLasers(playerPosition)
            firstPlayer.laserList.add(laser)
            soundManager.playShoot()


            multiplayerClient.sendLaserMessage()
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

                otherPlayer.hit(player.damage)
                laserIterator.remove()
                break
            }
        }
    }

    // called from player code when the current user looses or wins
    override fun onGameOver(position: PlayerPosition) {
        // you win
        if (position != playerPosition){
            game.showGameOverScreen(GameOverScreen(game,true, otherId))
        } else {
            game.showGameOverScreen(GameOverScreen(game,false, otherId))
        }

    }


}