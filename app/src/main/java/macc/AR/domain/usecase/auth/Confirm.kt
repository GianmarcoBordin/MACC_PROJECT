package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class Confirm(
    private val authManager: AuthManager
) {
     operator fun invoke(otp:String):Boolean{
        return authManager.confirm(otp)
    }
}
