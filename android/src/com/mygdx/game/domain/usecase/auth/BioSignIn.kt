package com.mygdx.game.domain.usecase.auth

import android.content.Context
import com.mygdx.game.domain.manager.AuthManager

class BioSignIn(
    private val authManager: AuthManager
){
    suspend operator fun invoke(context: Context,callback:(String) -> Unit){
        return authManager.bioSignIn(context,callback)
    }

}