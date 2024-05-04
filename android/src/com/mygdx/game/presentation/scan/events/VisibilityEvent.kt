package com.mygdx.game.presentation.scan.events

sealed class VisibilityEvent {
    data class Visible(val value: Boolean) : VisibilityEvent()
}