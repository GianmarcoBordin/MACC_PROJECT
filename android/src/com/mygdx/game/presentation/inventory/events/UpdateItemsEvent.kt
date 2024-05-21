package com.mygdx.game.presentation.inventory.events

sealed class UpdateItemsEvent {
    data object UpdateMergedItems : UpdateItemsEvent()
}