package com.mygdx.game.screen.component

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.Constants.WORLD_HEIGHT
import com.mygdx.game.Constants.WORLD_WIDTH
import com.mygdx.game.GameManager

// TODO use gameManager to change text skin
class HeadsUp(batch: SpriteBatch?, firstPlayerId: String, secondPlayerId: String) : Disposable {

    private var stage: Stage
    private val viewport: Viewport

    //Scene2D widgets
    private val firstPlayerName: Label
    private val secondPlayerName: Label
    private val versusLabel: Label

    init {

        viewport = ExtendViewport(WORLD_WIDTH * 2.5f , WORLD_HEIGHT * 2.5f , OrthographicCamera())
        stage = Stage(viewport, batch)

        //define a table used to organize our hud's labels
        val table = Table()
        //Top-Align table
        table.top()
        //make the table fill the entire stage
        table.setFillParent(true)

        // Load the font with your desired size
        val font = BitmapFont()

        // Define labels using the String, and a Label style consisting of the font and color
        firstPlayerName = Label(firstPlayerId, Label.LabelStyle(font, Color.WHITE))
        secondPlayerName = Label(secondPlayerId, Label.LabelStyle(font, Color.WHITE))
        versusLabel = Label("VS", Label.LabelStyle(font, Color.WHITE))

        //add our labels to our table, padding the top, and giving them all equal width with expandX
        table.add(firstPlayerName).expandX().padTop(3f)
        table.add(versusLabel).expandX().padTop(3f)
        table.add(secondPlayerName).expandX().padTop(3f)
        //add a second row to our table

        //add our table to the stage
        stage.addActor(table)
    }

    fun draw() {
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

}