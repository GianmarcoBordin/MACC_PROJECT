package com.mygdx.game.data.manager

import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.InventoryManager
import javax.inject.Inject

class InventoryManagerImpl @Inject constructor(private val dataRepository: DataRepository) : InventoryManager {
    override suspend fun retrieveItems(username: String): List<List<String>> {
        val list: MutableList<List<String>> = mutableListOf()
        for (i in 0 until 5) {
            dataRepository.getGameItem(username, i.toString()).observeForever { gameItemList ->
                if (gameItemList.isNotEmpty()) {
                    val stringGameItem = gameItemList?.get(0)?.split(" ") ?: emptyList()
                    list.add(stringGameItem)
                }
            }
        }
        return list
    }
}