package macc.AR.domain.manager

import kotlinx.coroutines.flow.Flow


interface LocalUserManager {
    suspend fun saveAppEntry()
    fun readAppEntry(): Flow<Boolean>

    suspend fun saveAnchorId(anchorId: String)
    fun readAnchorId(): Flow<String>

}