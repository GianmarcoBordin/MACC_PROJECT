package com.mygdx.game.player

enum class PlayerPosition {
    LEFT,
    RIGHT;

    fun opposite(): PlayerPosition {
        return when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }

}