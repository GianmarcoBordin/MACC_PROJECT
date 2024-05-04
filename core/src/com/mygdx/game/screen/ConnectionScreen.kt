package com.mygdx.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.GameManager
import com.mygdx.game.multiplayer.MultiplayerClient
import com.mygdx.game.player.PlayerType


class ConnectionScreen(private val gameManager: GameManager, myId: String, otherId: String) : ScreenAdapter(), MultiplayerClient.MultiplayerListener {

    private val width = (Gdx.graphics.width/1.2).toFloat()
    private val height = (Gdx.graphics.height/1.2).toFloat()

    private val goBack = TextButton("Back", gameManager.gameSkin)
    private val message = Label("Waiting the other user...", gameManager.gameSkin, "big-black")

    private val stage: Stage = Stage(FitViewport(width,height))
    private val table: Table = Table()

    private var multiplayerClient : MultiplayerClient = MultiplayerClient(gameManager, myId, otherId)

    init {

        multiplayerClient.setMultiplayerListener(this)
        multiplayerClient.connect()


        table.setFillParent(true)
        table.center()

        goBack.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                //gameManager.showGameScreen(GameScreen(gameManager, multiplayerClient, PlayerType.GREEN))
                multiplayerClient.disconnect()
                gameManager.showStartScreen()
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        table.add(goBack).fillX().center().padTop(1f).padBottom(40f).row()
        table.add(message).center().padBottom(10f)

        stage.addActor(table)

    }

    // Called when you are ready to start the game between the players
    override fun onAdversaryPlayerTypeReceived(playerType: PlayerType) {
        Gdx.app.postRunnable{
            gameManager.showGameScreen(GameScreen(gameManager, multiplayerClient, playerType))
        }


    }

    // used when adversary disconnect
    override fun onDisconnectedAdversary() {
        // TODO implement score screen where user check if he has win or not
        multiplayerClient.disconnect()
        gameManager.showStartScreen()
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()

    }

    override fun resize(width: Int, height: Int) {}

    override fun dispose() {
        multiplayerClient.disconnect()
        stage.dispose()
    }
}