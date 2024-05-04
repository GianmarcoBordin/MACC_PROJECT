package com.mygdx.game.presentation.authentication.events

// signup event is embedded in this class

sealed class SignUpEvent {
    data class SignUp(val name: String, val email: String, val password: String, val confirmPass: String) : SignUpEvent()
}