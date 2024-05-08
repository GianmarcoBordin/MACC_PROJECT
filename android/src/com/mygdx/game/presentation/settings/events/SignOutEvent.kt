package com.mygdx.game.presentation.settings.events

sealed class SignOutEvent {
     data object SignOut : SignOutEvent()
}