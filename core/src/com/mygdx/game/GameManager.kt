package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.mygdx.game.screen.GameScreen
import com.mygdx.game.screen.StartScreen
import com.mygdx.game.screen.ConnectionScreen

/**
* Class that handles screens and resources of the game
* */
class GameManager(private val myId: String) : Game() {

    val gameSkin by lazy { Skin(Gdx.files.internal("skin/glassy-ui.json")) }
    private var startScreen: StartScreen? = null
    private var connectionScreen: ConnectionScreen? = null
    private var gameScreen: GameScreen? = null

    override fun create() {

        setScreen(getStartScreen())
    }

    override fun dispose() {
        gameScreen?.dispose()
        startScreen?.dispose()
        connectionScreen?.dispose()
    }

    fun showStartScreen() {
        setScreen(getStartScreen())
    }

    fun showWaitingScreen(otherId: String) {
        if (otherId.isEmpty() || myId == otherId) {
            // Handle invalid input
            return
        }
        setScreen(getConnectionScreen(otherId))
    }

    fun showGameScreen(newGameScreen: GameScreen) {
        gameScreen?.dispose()
        gameScreen = newGameScreen
        setScreen(gameScreen)
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
}