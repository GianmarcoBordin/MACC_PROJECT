package com.mygdx.game.presentation.event

import androidx.compose.ui.graphics.ImageBitmap
import com.mygdx.game.presentation.inventory.events.GameItemEvent

sealed class MultiplayerEvent {

    data object GetSkinEvent : MultiplayerEvent()
}