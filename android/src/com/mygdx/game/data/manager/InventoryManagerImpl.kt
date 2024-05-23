package com.mygdx.game.data.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.InventoryManager
import javax.inject.Inject

class InventoryManagerImpl @Inject constructor(private val dataRepository: DataRepository) : InventoryManager {
    override suspend fun getGameItemsUser(username: String): LiveData<List<String>> {
        val gameItemLiveData = MutableLiveData<List<String>>()

        dataRepository.getGameItemsUser(username).observeForever { gameItemList ->
            gameItemLiveData.value = gameItemList
        }

        return gameItemLiveData
    }

    override suspend fun mergeItems(mergedItems: List<GameItem>, itemsToDelete: List<GameItem>) {
        // first, delete all previously saved items
        itemsToDelete.forEach { item ->
            dataRepository.deleteGameItem(item)
        }
        // then, repopulate the db with the new merged items
        mergedItems.forEach { item ->
            dataRepository.postGameItem(item)
        }
    }

    override suspend fun deleteItems(gameItem: GameItem): LiveData<String> {
      return dataRepository.deleteGameItem(gameItem)
    }
}