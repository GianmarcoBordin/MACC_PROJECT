package com.mygdx.game.presentation.scan.events

import com.mygdx.game.data.dao.GameItem

sealed class GameItemEvent {
    data class SaveGameItem(val gameItem: GameItem, val itemId: Int) : GameItemEvent()
}