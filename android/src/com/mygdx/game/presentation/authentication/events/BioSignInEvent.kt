package com.mygdx.game.presentation.authentication.events

import android.content.Context

sealed class BioSignInEvent{
    data class BioSignIn(val context: Context, val callback:(String)->Unit) : BioSignInEvent()
}