package com.mygdx.game.presentation.map.events


sealed class UpdateMapEvent {
    data object MapUpdate : UpdateMapEvent()
}