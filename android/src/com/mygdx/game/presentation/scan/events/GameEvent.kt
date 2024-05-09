package com.mygdx.game.presentation.scan.events

import androidx.compose.ui.graphics.ImageBitmap


sealed class GameEvent {
    data class StartGame(val bullets: ImageBitmap) : GameEvent()
    data object ResetGame : GameEvent()
}