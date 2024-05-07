package com.mygdx.game.dto

import com.mygdx.game.player.PlayerType


data class CharacterType(
    val type: PlayerType,
    val imagePath: String,
    val hp: Int,
    val damage: Int
)