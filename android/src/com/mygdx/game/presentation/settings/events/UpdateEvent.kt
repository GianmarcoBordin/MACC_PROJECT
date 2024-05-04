package com.mygdx.game.presentation.settings.events



/*This class embeds the event that will sent from the UI to the view model.
* SaveAppEntry is a subclass representing an event of saving the app entry during onboarding*/
sealed class UpdateEvent {
    data class Update(val name:String, val email: String, val password: String) : UpdateEvent()
}