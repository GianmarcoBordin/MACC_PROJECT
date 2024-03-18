package macc.AR.compose.authentication.events

sealed class SignOutEvent(){
     data object SignOut : SignOutEvent()
}