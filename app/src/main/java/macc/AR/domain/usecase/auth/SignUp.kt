package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class SignUp(
    private val authManager: AuthManager
){
    suspend operator fun invoke(){
        // TODO something with auth manager
    }
}
