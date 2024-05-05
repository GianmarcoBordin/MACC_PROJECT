package com.mygdx.game.presentation.scan.events

import androidx.compose.ui.graphics.ImageBitmap

sealed class UpdateDatabaseEvent {
    data class IncrementItemStats(val hpIncrement: Int, val damageIncrement: Int) : GameEvent()
}