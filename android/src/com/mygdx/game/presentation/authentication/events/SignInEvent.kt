package com.mygdx.game.presentation.authentication.events


/*This class embeds the event that will sent from the UI to the view model.
* SaveAppEntry is a subclass representing an event of saving the app entry during onboarding*/
sealed class SignInEvent{
    data class SignIn(val email: String, val password: String) : SignInEvent()
}