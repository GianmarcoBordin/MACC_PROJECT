package com.mygdx.game.domain.manager

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Ownership

interface ARManager {
    suspend fun addGameItem(gameItem: GameItem)
    suspend fun getGameItem(username: String, rarity: String): LiveData<List<String>>
    suspend fun getGameItemsUser(username: String): LiveData<List<String>>

    suspend fun addOwnership(ownership: Ownership)
    suspend fun getOwnership(username: String, itemId: String): List<String>
}