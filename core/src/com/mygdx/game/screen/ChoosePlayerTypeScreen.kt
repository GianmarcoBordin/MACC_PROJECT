package com.mygdx.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.GameManager
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.player.PlayerType

class ChoosePlayerTypeScreen(private val game: GameManager) : ScreenAdapter()
{

    private val width = (Gdx.graphics.width/1.2).toFloat()
    private val height = (Gdx.graphics.height/1.2).toFloat()

    private val chooseButton = TextButton("Confirm", game.gameSkin)

    private val stage: Stage = Stage(FitViewport(width,height))
    private val table: Table = Table()

    private var selectedTable: Table? = null

    private val defaultDrawableTexture: Texture = Texture(Gdx.files.internal("images/nine-patches1.png")) // Load your border texture
    private val selectedDrawableTexture: Texture = Texture(Gdx.files.internal("images/nine-patches2.png")) // Load your border texture

    private val defaultDrawable = NinePatchDrawable(NinePatch(defaultDrawableTexture, 10, 10, 10, 10)) // Adjust the numbers according to your border image
    private val selectedDrawable = NinePatchDrawable(NinePatch(selectedDrawableTexture, 10, 10, 10, 10))

    private var selectedCharacter: CharacterType? = null
    override fun show() {

        chooseButton.isVisible = false
        addTitle()

        // Set table properties
        table.setFillParent(true)
        table.center()

        // TODO this data should be passed from outside
        // Define character data
        val characters = listOf(
            CharacterType(PlayerType.GREEN, "images/Gunner_Green_image.png", 100, 20),
            CharacterType(PlayerType.RED, "images/Gunner_Red.png", 120, 25),
            CharacterType(PlayerType.YELLOW, "images/Gunner_Yellow_image.png", 80, 30),
            CharacterType(PlayerType.BLACK, "images/Gunner_Black_image.png", 150, 15),
            CharacterType(PlayerType.BLUE, "images/Gunner_Blue_image.png", 90, 25)
        )

        val padding = width * 0.025f


        // create the table
        characters.forEach { character ->
            val characterTable = createCharacterTable(character)
            table.add(characterTable).pad(padding)

        }

        table.row()

        table.add(chooseButton).fillX().center().padLeft(450f).padRight(450f).padTop(60f).colspan(5)

        addConnectButtonListener()

        stage.addActor(table)
        Gdx.input.inputProcessor = stage

    }

    private fun addTitle(){
        val title = Label("Choose one player", game.gameSkin, "big-black")
        title.setFontScale(1.8f)
        title.setAlignment(Align.center)
        title.y = height * 5f / 6
        title.width = width
        stage.addActor(title)
    }

    private fun addConnectButtonListener(){
        chooseButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                chooseButton.isVisible = true

                println("Selected character type is: ${selectedCharacter?.type}")
                // TODO pass data between this screen and the game screen
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
    }



    private fun createCharacterTable(characterData: CharacterType): Table {
        val table = Table()

        table.background = defaultDrawable
        table.defaults().pad(5f)

        // TODO Add character image from texture atlas as in the game screen
        val drawable = TextureRegionDrawable(Texture(characterData.imagePath))
        drawable.setMinSize(170f,170f)
        val characterImage = ImageButton(drawable)

        characterImage.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

                chooseButton.isVisible = true

                selectedCharacter = characterData

                selectTable(table)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        characterImage.setSize(170f,170f)
        table.add(characterImage).center().padBottom(5f)
        table.row()

        // Add HP label
        val hpLabel = Label("HP: ${characterData.hp}", game.gameSkin, "big-black")
        hpLabel.setFontScale(0.7f)
        hpLabel.setAlignment(Align.left)
        table.add(hpLabel).align(Align.left)
        table.row()

        // Add damage label
        val damageLabel = Label("Damage: ${characterData.damage}", game.gameSkin, "big-black")
        damageLabel.setFontScale(0.7f)
        damageLabel.setAlignment(Align.right)
        table.add(damageLabel).align(Align.right)
        table.padTop(10f)

        return table
    }

    private fun selectTable(table: Table){
        if (selectedTable == table){
            //table is already selected do nothing
            return
        }
        // Deselect the previously selected table
        selectedTable?.background = defaultDrawable

        // Select the new table
        selectedTable = table
        selectedTable?.background = selectedDrawable
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
