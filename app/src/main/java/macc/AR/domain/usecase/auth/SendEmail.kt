package macc.AR.domain.usecase.auth

import macc.AR.domain.manager.AuthManager

class SendEmail(
    private val authManager: AuthManager
) {
    suspend operator fun invoke(){
        return authManager.sendEmail()
    }
}