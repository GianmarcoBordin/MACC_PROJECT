package com.mygdx.game.data.dao

import android.location.Location

data class Item(
    val itemId: Int,
    var itemRarity: Int,
    var distance: Double,
    val location: Location
)

