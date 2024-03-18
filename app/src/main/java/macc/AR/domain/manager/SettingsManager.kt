package macc.AR.domain.manager

import macc.AR.compose.authentication.UserProfileBundle
import macc.AR.data.manager.UpdateListener

interface SettingsManager {
    suspend fun update(name:String,email:String,password:String)
    suspend fun fetch(): UserProfileBundle?
    fun setUpdateListener(ref: UpdateListener)
}