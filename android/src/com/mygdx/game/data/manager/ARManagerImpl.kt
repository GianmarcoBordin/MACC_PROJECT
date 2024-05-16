package com.mygdx.game.data.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.ARManager
import javax.inject.Inject

class ARManagerImpl @Inject constructor(private val dataRepository: DataRepository): ARManager {
    override suspend fun addGameItem(gameItem: GameItem) {
        dataRepository.postGameItem(gameItem)
    }

    override suspend fun getGameItem(username: String, rarity: String): LiveData<List<String>> {
        val gameItemLiveData = MutableLiveData<List<String>>()

        dataRepository.getGameItem(username, rarity).observeForever { gameItemList ->
            val stringGameItem = if (gameItemList.isEmpty()) emptyList()
                                else gameItemList[0].split(" ")
            gameItemLiveData.value = stringGameItem
        }

        return gameItemLiveData
    }
}