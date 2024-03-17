package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class SignIn(
    private val authManager: AuthManager
){
    suspend operator fun invoke(email:String,password:String){
        authManager.signIn(email = email, password = password)
    }

}