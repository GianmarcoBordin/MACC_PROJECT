package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class SignOut(
    private val authManager: AuthManager
){
    suspend operator fun invoke(){
        authManager.signOut()
    }
}