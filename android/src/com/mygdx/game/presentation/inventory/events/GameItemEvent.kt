package com.mygdx.game.presentation.inventory.events

import androidx.compose.ui.graphics.ImageBitmap

sealed class GameItemEvent {
    data class UpdateBitmap(val index: Int, val bitmap: ImageBitmap) : GameItemEvent()
}