package com.mygdx.game.data.dao

import android.graphics.Bitmap

data class GameItem(
    var owner: String = "",
    val rarity: Int = 0,
    val hp: Int = 0,
    val damage: Int = 0,
    val bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Example default bitmap
)