package macc.AR.domain.usecase.auth

import macc.AR.data.manager.UpdateListener
import macc.AR.domain.manager.AuthManager

class Subscribe(
    private val authManager: AuthManager
){
    operator fun invoke(ref: UpdateListener){
        authManager.setUpdateListener(ref)
    }

}