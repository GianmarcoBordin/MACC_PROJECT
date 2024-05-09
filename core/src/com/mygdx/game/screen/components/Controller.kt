package com.mygdx.game.screen.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport

class Controller(gameBatch: SpriteBatch): Disposable {
    private var viewport: Viewport
    private var stage: Stage
    private var cam: OrthographicCamera = OrthographicCamera()

    var isFightPressed = false

    var isUpPressed = false
    var isDownPressed = false
    var isLeftPressed = false
    var isRightPressed = false

    init {

        viewport = ExtendViewport(600f, 480f, cam)

        stage = Stage(viewport, gameBatch)


        Gdx.input.inputProcessor = stage
        var table = Table()
        table.left().bottom()
        val upImg = Image(Texture("controller/up.png"))
        upImg.setSize(70f, 70f)
        upImg.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                isUpPressed = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                isUpPressed = false
            }
        })
        val downImg = Image(Texture("controller/down.png"))
        downImg.setSize(70f, 70f)
        downImg.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                isDownPressed = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                isDownPressed = false
            }
        })
        val rightImg = Image(Texture("controller/right.png"))
        rightImg.setSize(70f, 70f)
        rightImg.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                isRightPressed = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                isRightPressed = false
            }
        })
        val leftImg = Image(Texture("controller/left.png"))
        leftImg.setSize(70f, 70f)
        leftImg.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                isLeftPressed = true
                return true
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                isLeftPressed = false
            }
        })

        val fightImage = Image(Texture("controller/gunButton.png"))
        fightImage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isFightPressed = true
                return true
            }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                isFightPressed = false
            }
        })

        table = drawTable(table, upImg, downImg, leftImg, rightImg)

        fightImage.setSize(65f, 65f)
        fightImage.setPosition(0.75f * viewport.worldWidth , 0.05f * viewport.worldHeight)

        stage.addActor(table)
        stage.addActor(fightImage)
    }

    fun draw() {
        stage.draw()
    }


    private fun drawTable(table: Table, upImg: Image, downImg: Image, leftImg: Image, rightImg: Image): Table{
        table.add()
        table.add(upImg).size(upImg.width, upImg.height)
        table.add()
        table.row().pad(3f, 3f, 3f, 3f)
        table.add(leftImg).size(leftImg.width, leftImg.height)
        table.add()
        table.add(rightImg).size(rightImg.width, rightImg.height)
        table.row().padBottom(5f)
        table.add()
        table.add(downImg).size(downImg.width, downImg.height)
        table.add()

        return table
    }


    fun isMoving(): Boolean{
        return isLeftPressed || isRightPressed || isUpPressed || isDownPressed
    }

    override fun dispose() {
        stage.dispose()
    }

}