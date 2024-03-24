package macc.AR.compose.settings.events

sealed class SignOutEvent(){
     data object SignOut : SignOutEvent()
}