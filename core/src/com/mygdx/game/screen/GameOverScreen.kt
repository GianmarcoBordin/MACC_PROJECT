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
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.GameManager



class GameOverScreen(private val game: GameManager, private val win: Boolean, private val otherId: String) : ScreenAdapter() {

    private val width = (Gdx.graphics.width/1.2).toFloat()
    private val height = (Gdx.graphics.height/1.2).toFloat()

    private val stage: Stage = Stage(FitViewport(width,height))
    private val table: Table = Table()

    private val playAgainButton = TextButton("Play again", game.gameSkin)
    private val changeAdversaryButton = TextButton("Change adversary", game.gameSkin)
    private val exitButton = TextButton("Exit", game.gameSkin)
    init {

        addTitle(if (win) "You win!" else "You loose")
        // Set table properties
        table.setFillParent(true)
        table.center()


        playAgainButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

                game.showConnectionScreen(otherId = otherId)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        changeAdversaryButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                game.showStartScreen(otherId,disconnect = true)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        exitButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

                game.endActivity()
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        table.add(playAgainButton).fillX().center().padTop(40f).padBottom(20f).row()
        table.add(changeAdversaryButton).fillX().center().padTop(40f).padBottom(20f).row()

        stage.addActor(table)


    }

    private fun addTitle(text: String){
        val title = Label(text, game.gameSkin, "big-black")
        title.setFontScale(1.8f)
        title.setAlignment(Align.center)
        title.y = height * 4f / 5
        title.width = width
        stage.addActor(title)
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
        stage.dispose()
    }
}