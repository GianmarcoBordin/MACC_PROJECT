package com.mygdx.game.presentation.scan.events

sealed class UpdateMappingEvent {
    data object UpdateMapping : UpdateMappingEvent()

}