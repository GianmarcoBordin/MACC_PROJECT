package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class LogOut(
    private val authManager: AuthManager
){
    suspend operator fun invoke(){
        // TODO something with auth manager
    }
}