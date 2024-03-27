package macc.AR.domain.manager


import macc.AR.data.UserProfileBundle
import macc.AR.data.manager.UpdateListener

interface SettingsManager {
    suspend fun update(name:String,email:String,password:String)

    suspend fun signOut()
    suspend fun fetch(): UserProfileBundle?
    fun setUpdateListener(ref: UpdateListener)
}