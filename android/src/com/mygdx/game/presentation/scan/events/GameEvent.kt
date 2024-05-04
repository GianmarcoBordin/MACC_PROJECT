package com.mygdx.game.presentation.scan.events

import androidx.compose.ui.graphics.ImageBitmap


sealed class GameEvent {
    data class StartGame(val item: ImageBitmap, val bullets: ImageBitmap) : GameEvent()
}