package com.mygdx.game.presentation.scan.events

sealed class UpdateDatabaseEvent {
    data class IncrementItemStats(val hpIncrement: Int, val damageIncrement: Int) : UpdateDatabaseEvent()
    data object AddItem : UpdateDatabaseEvent()
    data class AddOwnership(val itemId: String) : UpdateDatabaseEvent()


}