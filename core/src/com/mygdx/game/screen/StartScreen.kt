package com.mygdx.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.GameManager
import com.mygdx.game.dto.CharacterType

class StartScreen(private val game: GameManager) : ScreenAdapter()
{

    private val width = (Gdx.graphics.width/1.2).toFloat()
    private val height = (Gdx.graphics.height/1.2).toFloat()

    private val exitButton = TextButton("Exit", game.gameSkin)
    private val connectButton = TextButton("Confirm", game.gameSkin)

    private val stage: Stage = Stage(FitViewport(width,height))
    private val table: Table = Table()

    private var selectedTable: Table? = null

    private var textureAtlas: TextureAtlas = TextureAtlas("atlas/images.atlas")
    private val defaultDrawableTexture: TextureRegion = textureAtlas.findRegion("nine-patches1")
    private val selectedDrawableTexture: TextureRegion = textureAtlas.findRegion("nine-patches2")

    private val defaultDrawable = NinePatchDrawable(NinePatch(defaultDrawableTexture, 10, 10, 10, 10)) // Adjust the numbers according to your border image
    private val selectedDrawable = NinePatchDrawable(NinePatch(selectedDrawableTexture, 10, 10, 10, 10))

    private val userIdField =  TextField("", game.gameSkin)
    private val ipAddressField = TextField("", game.gameSkin)
    private val errorLabel = Label("User id or server ip is empty!", game.gameSkin, "big-black")
    private val noCollectedSkinLabel = Label("Please first try to find a skin on the map and catch it!", game.gameSkin, "big-black")

    var selectedCharacter: CharacterType? = null

    init {

        connectButton.isVisible = false
        noCollectedSkinLabel.isVisible = false
        userIdField.messageText = "1234"
        ipAddressField.messageText = "Enter IP address"
        addTitle()

        // Set table properties
        table.setFillParent(true)
        table.bottom().padBottom(20f)

        addUserIdFieldTable()

        val padding = width * 0.025f

        if (game.myCollectedSkin.isEmpty()){
            noCollectedSkinLabel.isVisible = true
            table.add(noCollectedSkinLabel).fillX().center().padLeft(450f).padRight(450f).padTop(60f).padBottom(40f).colspan(5)
        }

        // create the table
        game.myCollectedSkin.forEach { character ->
            val characterTable = createCharacterTable(character)
            table.add(characterTable).padLeft(padding).padRight(padding).padTop(200f)
        }

        table.row()

        table.add(connectButton).fillX().center().padLeft(450f).padRight(450f).padTop(60f).padBottom(40f).colspan(5)


        addConnectButtonListener()

        stage.addActor(table)

        addExitButtonListener()
        exitButton.setPosition(width * 0.05f, height * 0.8f)
        exitButton.setSize(width / 10f, height / 7)
        stage.addActor(exitButton)

        Gdx.input.inputProcessor = stage
    }

    override fun show() {
        Gdx.input.inputProcessor = stage

    }

    private fun addExitButtonListener(){
        exitButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                game.endActivity()
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
    }

    fun handleConnection(){

        if (userIdField.text.isNotEmpty() && checkIpv4Address(ipAddressField.text) && selectedCharacter != null){
            errorLabel.isVisible = false
            val playerId = userIdField.text
            val ipAddress = ipAddressField.text

            game.showConnectionScreen(playerId, ipAddress)

        } else {
            errorLabel.isVisible = true
        }

    }

    private fun checkIpv4Address(ip: String): Boolean{
        val ipAddressPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$".toRegex()
        return ip.matches(ipAddressPattern)
    }

    private fun addUserIdFieldTable(){
        val labelTable = Table()
        labelTable.setFillParent(true)
        labelTable.center().padBottom(350f)

        val userIdLabel = Label("Insert adversary user id", game.gameSkin, "big-black")
        labelTable.add(userIdLabel).left().padBottom(5f).padTop(55f).row()
        labelTable.add(userIdField).width(width / 3).center().padBottom(20f).row()

        val ipAddressLabel = Label("Insert server IP address", game.gameSkin, "big-black")
        labelTable.add(ipAddressLabel).left().padBottom(5f).row()
        labelTable.add(ipAddressField).width(width / 3).center().padBottom(80f).row()


        stage.addActor(labelTable)


        errorLabel.isVisible = false
        errorLabel.setPosition(width/3.5f, 160f)
        stage.addActor(errorLabel)
    }

    private fun addTitle(){
        val title = Label("Choose one player", game.gameSkin, "big-black")
        title.setFontScale(1.6f)
        title.setAlignment(Align.center)
        title.y = height * 5.4f / 6
        title.width = width
        stage.addActor(title)
    }

    private fun addConnectButtonListener(){
        connectButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                handleConnection()
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


        val drawable = TextureRegionDrawable(textureAtlas.findRegion(characterData.type.getSkinRegionName()))
        drawable.setMinSize(110f,110f)
        val characterImage = ImageButton(drawable)

        characterImage.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

                connectButton.isVisible = true

                selectedCharacter = characterData

                selectTable(table)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        characterImage.setSize(110f,110f)
        table.add(characterImage).center().padBottom(2f)
        table.row()

        // Add HP label
        val hpLabel = Label("HP: ${characterData.hp}", game.gameSkin, "big-black")
        hpLabel.setFontScale(0.6f)
        hpLabel.setAlignment(Align.left)
        table.add(hpLabel).align(Align.left)
        table.row()

        // Add damage label
        val damageLabel = Label("Damage: ${characterData.damage}", game.gameSkin, "big-black")
        damageLabel.setFontScale(0.6f)
        damageLabel.setAlignment(Align.right)
        table.add(damageLabel).align(Align.right)
        table.padTop(3f)


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

        if(userIdField.text.isNotEmpty() && ipAddressField.text.isNotEmpty()){
            errorLabel.isVisible = false
        }

        stage.act(delta)
        stage.draw()


    }



    override fun resize(width: Int, height: Int) {}

    override fun dispose() {
        stage.dispose()
    }
}
