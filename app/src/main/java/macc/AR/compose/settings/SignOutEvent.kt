package macc.AR.compose.settings

sealed class SignOutEvent(){
     data object SignOut : SignOutEvent()
}