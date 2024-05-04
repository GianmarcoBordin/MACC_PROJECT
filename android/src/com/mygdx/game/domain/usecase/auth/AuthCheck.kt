package com.mygdx.game.domain.usecase.auth

import com.mygdx.game.domain.manager.AuthManager

class AuthCheck(
    private val authManager: AuthManager
){
     operator fun invoke():Boolean{
        return authManager.authCheck()
    }

}