package com.mygdx.game.data.dao

import android.graphics.Bitmap

data class GameItem(
    val id: String,
    val rarity: Int,
    val hp: Int,
    val damage: Int,
    val bitmap: Bitmap
)