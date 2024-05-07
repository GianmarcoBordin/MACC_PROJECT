package com.mygdx.game.dto

import com.mygdx.game.player.PlayerPosition


enum class MessageType {
    INIT,
    START,
    DISCONNECTED,
    MOVEMENT,
    LASER
}

data class WebSocketMessage(
    val type: MessageType,
    val senderId: String? = null,
    val receiverId: String? = null,
    val newX: Float? = null,
    val newY: Float? = null,
    val playerPosition: PlayerPosition? = null,
    val playerType: CharacterType? = null
)


data class InitMessage(
    val senderId: String,
    val receiverId: String,
    val playerPosition: PlayerPosition,
    val playerType: CharacterType
) : Message(MessageType.INIT)






