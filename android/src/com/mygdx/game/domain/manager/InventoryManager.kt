package com.mygdx.game.domain.manager

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem

interface InventoryManager {
    suspend fun getGameItemsUser(username: String): LiveData<List<String>>
    suspend fun mergeItems(mergedItems: List<GameItem>, itemsToDelete: List<GameItem>)
    suspend fun deleteItems(gameItem: GameItem) :  LiveData<String>
}