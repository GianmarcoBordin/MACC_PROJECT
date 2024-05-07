package com.mygdx.game.dto

import com.mygdx.game.player.PlayerSkin


data class CharacterType(
    val type: PlayerSkin,
    val hp: Int,
    val damage: Int
)