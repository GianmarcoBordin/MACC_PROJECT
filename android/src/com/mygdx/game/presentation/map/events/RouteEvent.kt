package com.mygdx.game.presentation.map.events

import android.location.Location


sealed class RouteEvent {
    data class Route(val from: Location?, val to: Location) : RouteEvent()
}