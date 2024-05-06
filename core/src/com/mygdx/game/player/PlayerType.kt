package com.mygdx.game.player

enum class PlayerType {
    GREEN,
    RED,
    BLACK,
    YELLOW,
    BLUE;

    fun opposite(): PlayerType {
        return when (this) {
            GREEN -> RED
            RED -> GREEN
            BLACK -> TODO()
            YELLOW -> TODO()
            BLUE -> TODO()
        }
    }
}