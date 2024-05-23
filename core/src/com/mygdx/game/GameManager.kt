package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.screen.GameScreen
import com.mygdx.game.screen.ConnectionScreen
import com.mygdx.game.screen.GameOverScreen
import com.mygdx.game.screen.StartScreen

/**
 * Class that handles screens and resources of the game
 */

interface GameTerminationListener {
    fun onGameTerminated()
    fun submitScore(username: String, win: Boolean)
}


class GameManager(val myCollectedSkin: ArrayList<CharacterType>, private val myId: String, private val username: String) : Game() {

    val gameSkin by lazy { Skin(Gdx.files.internal("skin/glassy-ui.json")) }
    private var startScreen: StartScreen? = null
    private var connectionScreen: ConnectionScreen? = null
    private var gameScreen: GameScreen? = null
    private var gameOverScreen: GameOverScreen? = null
    private var gameTerminationListener : GameTerminationListener? = null

    fun setTerminationListener(listener: GameTerminationListener){
        this.gameTerminationListener = listener
    }


    override fun create() {
        setScreen(getStartScreen())

    }

    override fun dispose() {
        gameScreen?.dispose()
        startScreen?.dispose()
        connectionScreen?.dispose()
        gameOverScreen?.dispose()
    }

    fun showStartScreen(adversaryId: String, disconnect: Boolean) {
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



    private fun getStartScreen(): StartScreen {
        if (startScreen == null) {
            startScreen = StartScreen(this)
        }

        return startScreen!!
    }


    private fun getConnectionScreen(otherId: String): ConnectionScreen {
        if (connectionScreen == null) {
            connectionScreen =
                startScreen?.selectedCharacter?.let { ConnectionScreen(this, username, otherId, it) }
        }

        return connectionScreen!!
    }


    /*
    this method is called from exit button in the game over instance and it's
    used to change activity
     */
    fun endActivity(){
        gameTerminationListener?.onGameTerminated()
    }

    fun endActivityAndSubmitScore(win: Boolean){
        gameTerminationListener?.submitScore(username, win)
    }


}
