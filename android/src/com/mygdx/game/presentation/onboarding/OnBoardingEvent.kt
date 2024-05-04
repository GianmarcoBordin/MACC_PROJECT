package com.mygdx.game.presentation.onboarding

/*This class embeds the event that will sent from the UI to the view model.
* SaveAppEntry is a subclass representing an event of saving the app entry during onboarding*/
sealed class OnBoardingEvent{
    data object SaveAppEntry: OnBoardingEvent()
}