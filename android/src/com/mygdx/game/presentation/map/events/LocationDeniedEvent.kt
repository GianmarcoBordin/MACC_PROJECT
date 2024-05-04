package com.mygdx.game.presentation.map.events


sealed class LocationDeniedEvent{
    data object LocationDenied : LocationDeniedEvent()
}