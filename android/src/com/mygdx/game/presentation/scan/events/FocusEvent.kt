package com.mygdx.game.presentation.scan.events


sealed class FocusEvent {
    data class Focus(val value: Boolean) : FocusEvent()
}