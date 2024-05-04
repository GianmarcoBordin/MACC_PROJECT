package com.mygdx.game.domain.manager

import android.location.Location
import kotlinx.coroutines.flow.Flow
import com.mygdx.game.data.dao.Biometric
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.data.manager.UpdateListener


interface LocalUserManager {
    suspend fun saveAppEntry()
    fun readAppEntry(): List<Boolean>
    suspend fun clearAppEntry()
    fun getUserProfile(): UserProfileBundle
    suspend fun saveUserProfile(userProfile: UserProfileBundle)
    suspend fun saveAnchorId(anchorId: String)
    fun readAnchorId(): Flow<String>
    suspend fun saveBio(biometric: Biometric)
    fun readBio():Pair<String,String>
    suspend fun saveScore(rank: Rank)
    fun readScore(): Rank
    fun readLocation():Location?
    fun startLocUpdates()
    fun stopLocUpdates()

    fun setUpdateListener(ref: UpdateListener)
}