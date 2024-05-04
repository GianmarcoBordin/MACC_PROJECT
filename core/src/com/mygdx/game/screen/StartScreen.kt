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
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.GameManager
import com.mygdx.game.player.PlayerType


class StartScreen(private val game: GameManager) : ScreenAdapter() {
    private val width = (Gdx.graphics.width/1.2).toFloat()
    private val height = (Gdx.graphics.height/1.2).toFloat()

    private val userIdField =  TextField("", game.gameSkin)
    private val connectButton = TextButton("Connect", game.gameSkin)
    private val errorLabel = Label("Please first insert user id!", game.gameSkin, "big-black")
    private val stage: Stage = Stage(FitViewport(width,height))
    private val table: Table = Table()


    init {

        connectButton.isDisabled = true
        userIdField.messageText = "1234"
        addTitle()

        // Set table properties
        table.setFillParent(true)
        table.center()

        val userIdLabel = Label("Insert adversary user id", game.gameSkin, "big-black")
        table.add(userIdLabel).left().padBottom(10f).padTop(30f).row()
        table.add(userIdField).width(width / 3).center().padBottom(40f).row()


        connectButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                handleConnection()
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        table.add(connectButton).fillX().center().padTop(40f).padBottom(20f) .row()


        errorLabel.isVisible = false
        table.add(errorLabel).left().padBottom(10f).row()

        stage.addActor(table)


    }

    private fun addTitle(){
        val title = Label("Waiting room", game.gameSkin, "big-black")
        title.setFontScale(1.8f)
        title.setAlignment(Align.center)
        title.y = height * 4f / 5
        title.width = width
        stage.addActor(title)
    }

    fun handleConnection(){

        // TODO condition to check userid
        if (userIdField.text.isNotEmpty()){
            errorLabel.isVisible = false
            val playerId = userIdField.text

            game.showWaitingScreen(playerId)

        } else {
            errorLabel.isVisible = true
        }

    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()

        if(userIdField.text.isNotEmpty()){
            connectButton.isDisabled = false

        }

    }

    override fun resize(width: Int, height: Int) {}

    override fun dispose() {
        stage.dispose()
    }
}