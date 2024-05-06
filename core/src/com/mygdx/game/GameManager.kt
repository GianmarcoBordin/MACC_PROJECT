package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.mygdx.game.multiplayer.MultiplayerClient
import com.mygdx.game.player.PlayerType
import com.mygdx.game.screen.GameScreen
import com.mygdx.game.screen.StartScreen
import com.mygdx.game.screen.ConnectionScreen
import com.mygdx.game.screen.GameOverScreen
import com.mygdx.game.screen.ChoosePlayerTypeScreen

/**
 * Class that handles screens and resources of the game
 */
class GameManager(private val myId: String) : Game() {

    val gameSkin by lazy { Skin(Gdx.files.internal("skin/glassy-ui.json")) }
    private var startScreen: StartScreen? = null
    private var connectionScreen: ConnectionScreen? = null
    private var gameScreen: GameScreen? = null
    private var gameOverScreen: GameOverScreen? = null
    private var choosePlayerTypeScreen: ChoosePlayerTypeScreen? = null

    override fun create() {
        //showGameScreen(GameScreen(this, MultiplayerClient(this,"1","2"),PlayerType.GREEN,"me","other"))
        //setScreen(getStartScreen())
        showChoosePlayerTypeScreen()
    }

    override fun dispose() {
        gameScreen?.dispose()
        startScreen?.dispose()
        connectionScreen?.dispose()
        choosePlayerTypeScreen?.dispose()
    }

    fun showStartScreen(adversaryId: String, disconnect: Boolean) {
        // TODO disconnect the client could be improved
        if (disconnect){
            getConnectionScreen(adversaryId).multiplayerClient.disconnect()
        }
        setScreen(getStartScreen())
    }

    fun showConnectionScreen(otherId: String) {
        if (otherId.isEmpty() || myId == otherId) {
            // Handle invalid input
            return
        }
        // useful when you call the method from the game over screen and you need to disconnect the user

        setScreen(getConnectionScreen(otherId))
    }

    fun showGameOverScreen(newGameOverScreen: GameOverScreen) {
        gameOverScreen?.dispose()
        gameOverScreen = newGameOverScreen
        setScreen(gameOverScreen)
    }

    fun showGameScreen(newGameScreen: GameScreen) {
        gameScreen?.dispose()
        gameScreen = newGameScreen
        setScreen(gameScreen)
    }

    fun showChoosePlayerTypeScreen() {
        setScreen(getChoosePlayerTypeScreen())
    }

    private fun getStartScreen(): StartScreen {
        if (startScreen == null) {
            startScreen = StartScreen(this)
        }

        return startScreen!!
    }

    private fun getConnectionScreen(otherId: String): ConnectionScreen {
        if (connectionScreen == null) {
            connectionScreen = ConnectionScreen(this, myId, otherId)
        }

        return connectionScreen!!
    }


    private fun getChoosePlayerTypeScreen(): ChoosePlayerTypeScreen {
        if (choosePlayerTypeScreen == null) {
            choosePlayerTypeScreen = ChoosePlayerTypeScreen(this)
        }
        return choosePlayerTypeScreen!!
    }
}
