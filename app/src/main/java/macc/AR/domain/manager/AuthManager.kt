package macc.AR.domain.manager

import macc.AR.data.manager.UpdateListener

interface AuthManager {
    suspend fun signIn(email:String,password:String)
    suspend fun signUp():Boolean
    suspend fun logOut():Boolean
    fun setUpdateListener(ref:UpdateListener)
}