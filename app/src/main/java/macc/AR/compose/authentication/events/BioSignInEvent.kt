package macc.AR.compose.authentication.events

import android.content.Context

sealed class BioSignInEvent(){
    data class BioSignIn(val context: Context, val callback:(Boolean)->Unit) : BioSignInEvent()
}