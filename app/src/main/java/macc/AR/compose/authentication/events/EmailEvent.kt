package macc.AR.compose.authentication.events


sealed class EmailEvent(){
    data object Email: EmailEvent()
}