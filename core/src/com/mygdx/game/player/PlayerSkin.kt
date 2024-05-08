package com.mygdx.game.player

enum class PlayerSkin {
    GREEN,
    RED,
    YELLOW,
    BLUE,
    BLACK;

    fun getSkinRegionName(): String {
        return when (this) {
            GREEN -> "Gunner_Green_image"
            RED -> "Gunner_Red_image"
            BLACK -> "Gunner_Black_image"
            YELLOW -> "Gunner_Yellow_image"
            BLUE -> "Gunner_Blue_image"
        }
    }
}