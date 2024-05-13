package com.mygdx.game.data.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
}