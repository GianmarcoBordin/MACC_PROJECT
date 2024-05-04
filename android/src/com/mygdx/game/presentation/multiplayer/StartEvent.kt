package com.mygdx.game.presentation.multiplayer



sealed class StartEvent{
    data object startEvent : StartEvent()
}