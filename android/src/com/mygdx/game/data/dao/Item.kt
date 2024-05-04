package com.mygdx.game.data.dao

import android.location.Location

data class Item(
    val itemId: String,
    var itemName: String,
    var itemRarity: String,
    var distance:Double,
    val location: Location,
    val imageUrl: String?
)
