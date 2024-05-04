package com.mygdx.game.domain.manager


import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.data.manager.UpdateListener

interface SettingsManager {
    suspend fun update(name:String,email:String,password:String)

    suspend fun signOut()
    suspend fun fetch(): UserProfileBundle?
    fun setUpdateListener(ref: UpdateListener)
}