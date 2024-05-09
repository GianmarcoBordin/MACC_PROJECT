package com.mygdx.game.multiplayer

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import com.mygdx.game.GameManager
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.dto.WebSocketMessage
import com.mygdx.game.dto.InitMessage
import com.mygdx.game.dto.Message
import com.mygdx.game.dto.MessageType
import com.mygdx.game.player.PlayerPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


const val debugTag = "CLIENT"


class MultiplayerClient(
    private val gameManager: GameManager,
    private var myPlayerId: String,
    private var adversaryId: String,
    private val characterType: CharacterType
) {

    interface MultiplayerListener {
        fun onAdversaryPlayerTypeReceived(playerPosition: PlayerPosition, playerType: CharacterType)
        fun onDisconnectedAdversary()
    }

    interface GameEventListener {
        fun onMovementMessage(newX: Float,newY: Float)
        fun onLaserMessage()
    }

    private lateinit var webSocket: WebSocket
    private var client : OkHttpClient = OkHttpClient()
    private val webSocketUrl = "ws://192.168.1.34:9000"
    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var gson = Gson()


    private var multiplayerListener: MultiplayerListener? = null
    private var gameEventListener: GameEventListener? = null

    fun setMultiplayerListener(listener: MultiplayerListener) {
        multiplayerListener = listener
    }

    fun setGameEventListener(listener: GameEventListener) {
        gameEventListener = listener
    }


    fun connect(){
        webSocket = client.newWebSocket(Request.Builder().url(webSocketUrl).build(),
            object : WebSocketListener(){
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Gdx.app.log(debugTag, "INIT MESSAGE")
                    val message : Message = InitMessage(myPlayerId, adversaryId, PlayerPosition.LEFT, characterType)
                    val json = gson.toJson(message)

                    // note: you cannot send it into a coroutine because in this way we need a blocking call to server
                    webSocket.send(json)

                }

                override fun onMessage(webSocket: WebSocket, text: String) {

                    val message = gson.fromJson(text, WebSocketMessage::class.java)

                    when (message.type) {

                        MessageType.MOVEMENT -> {
                            // used to avoid null value
                            message.newX?.let { message.newY?.let { it1 ->
                                gameEventListener?.onMovementMessage(it, it1)
                            } }

                        }

                        MessageType.LASER -> {
                            gameEventListener?.onLaserMessage()
                        }

                        MessageType.START -> {
                            // Handle START message
                            println(message)
                            if(message.senderId == adversaryId){
                                Gdx.app.log(debugTag, "connection established, you can start sending messages")

                                Gdx.app.postRunnable{
                                    message.playerType?.let {
                                        message.playerPosition?.let { it1 ->
                                            multiplayerListener?.onAdversaryPlayerTypeReceived(
                                                it1,it
                                            )
                                        }
                                    }
                                }


                            } else {
                                Gdx.app.log(debugTag, "$adversaryId not recognized")

                            }

                        }

                        MessageType.DISCONNECTED -> {
                            // Handle DISCONNECTED message
                            Gdx.app.log(debugTag, "${message.senderId} disconnected, you cannot play")

                            multiplayerListener?.onDisconnectedAdversary()
                        }

                        MessageType.INIT -> {
                        }

                    }

                }


                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    // WebSocket connection closed
                    Gdx.app.log(debugTag, "connection closed: $reason")
                    
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    // WebSocket connection failure
                    Gdx.app.log(debugTag, "connection failure: $response - $t")
                    // go back to menu screen
                    this@MultiplayerClient.disconnect()
                    gameManager.showStartScreen(adversaryId, false)


                }


            }
        )

    }

    fun disconnect() {
        try {
            webSocket.close(1000, "Disconnected by user")
            coroutineScope.cancel()
        } catch (e: Exception) {
            Gdx.app.log(debugTag, e.message)
        }
    }
    
    fun sendPlayerMovementMessage(newX: Float, newY: Float) {
        // Last two are dummy
        val message = WebSocketMessage(MessageType.MOVEMENT, receiverId = adversaryId, newX = newX, newY = newY)
        val json = gson.toJson(message)

        webSocket.send(json)


    }

    fun sendLaserMessage() {

        val message = WebSocketMessage(type = MessageType.LASER, receiverId = adversaryId)
        val json = gson.toJson(message)

        webSocket.send(json)

    }



}