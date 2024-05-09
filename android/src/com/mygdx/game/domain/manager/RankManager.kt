package com.mygdx.game.domain.manager

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.manager.UpdateListener


interface RankManager{
    suspend fun fetch(): LiveData<List<String>>

    fun setUpdateListener(ref: UpdateListener)
    suspend fun fetchGameItem(user: String, rarity: String): LiveData<List<String>>
    suspend fun updateUserLocation(player: Player): LiveData<String>
}