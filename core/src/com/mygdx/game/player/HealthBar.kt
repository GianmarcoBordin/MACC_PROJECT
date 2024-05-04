package com.mygdx.game.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport

class HealthBar(gameBatch: SpriteBatch) {

    private var viewport: Viewport
    private var stage: Stage
    private var healthBox = Rectangle()
    private var cam: OrthographicCamera = OrthographicCamera()
    private val table: Table = Table()
    private val healthTexture = Texture("images/blank.png")

    // TODO
    init {
        viewport = ExtendViewport(600f, 480f, cam)
        stage = Stage(viewport, gameBatch)
        Gdx.input.inputProcessor = stage


    }

}

