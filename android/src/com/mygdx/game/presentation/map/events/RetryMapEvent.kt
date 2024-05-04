package com.mygdx.game.presentation.map.events

sealed class RetryMapEvent {
    data object MapRetry : RetryMapEvent()
}