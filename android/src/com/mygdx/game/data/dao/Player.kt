package com.mygdx.game.data.dao

import android.location.Location

data class Player(
    val username: String,
    val location: Location,
    var distance: Double,
    val avatarUrl: String?
) {
    // Custom properties and methods can be added here
}