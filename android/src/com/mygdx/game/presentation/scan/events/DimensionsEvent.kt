package com.mygdx.game.presentation.scan.events
sealed class DimensionsEvent {
    data class Dimensions (val width: Int, val height: Int) : DimensionsEvent()
}