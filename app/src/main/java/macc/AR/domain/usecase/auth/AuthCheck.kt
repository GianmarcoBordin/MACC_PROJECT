package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class AuthCheck(
    private val authManager: AuthManager
){
     operator fun invoke():Boolean{
        return authManager.authCheck()
    }

}