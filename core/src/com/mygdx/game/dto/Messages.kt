package com.mygdx.game.dto

import com.mygdx.game.player.PlayerType


enum class MessageType {
    INIT,
    START,
    MOVEMENT,
    LASER,
    DISCONNECTED
}

data class GenericMessage(
    val type: MessageType,
    val senderId: String,
    val receiverId: String,
    val newX: Float,
    val newY: Float,
    val playerType: PlayerType
)


data class InitMessage(
    val senderId: String,
    val receiverId: String,
    val playerType: PlayerType
) : Message(MessageType.INIT)






