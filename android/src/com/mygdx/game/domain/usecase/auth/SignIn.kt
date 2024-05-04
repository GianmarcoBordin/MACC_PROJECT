package com.mygdx.game.domain.usecase.auth

import com.mygdx.game.domain.manager.AuthManager

class SignIn(
    private val authManager: AuthManager
){
    suspend operator fun invoke(email:String,password:String){
        authManager.signIn(email = email, password = password)
    }

}