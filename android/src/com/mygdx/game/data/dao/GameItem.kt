package com.mygdx.game.data.dao

import android.graphics.Bitmap

data class GameItem(
    var owner: String = "",
    val itemId: Int = 0,
    val rarity: Int = 0,
    var hp: Int = 0,
    var damage: Int = 0,
    val bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Example default bitmap
)