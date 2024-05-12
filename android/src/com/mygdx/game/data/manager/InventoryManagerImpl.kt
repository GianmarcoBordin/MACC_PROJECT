package com.mygdx.game.data.manager

import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.InventoryManager
import javax.inject.Inject

class InventoryManagerImpl @Inject constructor(private val dataRepository: DataRepository) : InventoryManager {
    override suspend fun retrieveItems(username: String): List<List<String>> {
        val list: MutableList<List<String>> = mutableListOf()
        dataRepository.getGameItemsUser(username).observeForever { gameItemList ->
            if (gameItemList.isNotEmpty()) {
                for (i in 0 until gameItemList.size) {
                    val stringGameItem = gameItemList?.get(i)?.split(" ") ?: emptyList()
                    list.add(stringGameItem)
                }
            }
        }
        return list
    }
}