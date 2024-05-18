package com.mygdx.game.presentation.scan.events

sealed class UpdateMappingEvent {
    data class UpdateMapping(val itemId: String) : UpdateMappingEvent()
}