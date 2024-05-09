package com.mygdx.game.data.dao

import android.location.Location

data class Player(
    val username: String,
    var location: Location,
    var distance: Double
) {
    // Custom properties and methods can be added here
}