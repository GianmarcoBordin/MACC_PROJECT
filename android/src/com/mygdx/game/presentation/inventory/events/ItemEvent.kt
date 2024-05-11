package com.mygdx.game.presentation.inventory.events

sealed class ItemEvent {
    data object RetrieveItems : ItemEvent()
}