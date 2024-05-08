package com.mygdx.game.presentation.scan.events

sealed class DataStoreEvent {
    data object readDataStore : DataStoreEvent()
}