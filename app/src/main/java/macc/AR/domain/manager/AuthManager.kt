package macc.AR.domain.manager

import macc.AR.data.manager.UpdateListener

interface AuthManager {
    suspend fun signIn(email:String,password:String)
    suspend fun signUp(name:String,email:String,password:String,confirmPass:String)
    suspend fun signOut()
    fun setUpdateListener(ref:UpdateListener)
}