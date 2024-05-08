package com.mygdx.game.dto

import com.mygdx.game.player.PlayerSkin
import java.io.Serializable


data class CharacterType(
    val type: PlayerSkin,
    val hp: Int,
    val damage: Int
) : Serializable