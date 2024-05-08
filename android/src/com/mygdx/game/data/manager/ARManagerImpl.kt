package com.mygdx.game.data.manager

import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Ownership
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.ARManager
import javax.inject.Inject

class ARManagerImpl @Inject constructor(private val dataRepository: DataRepository): ARManager {
    override suspend fun addGameItem(gameItem: GameItem) {
        dataRepository.postGameItem(gameItem)
    }

    override suspend fun getGameItem(username: String, rarity: String): List<String> {
        val listOfStringGameItem = dataRepository.getGameItem(username, rarity).value?.get(0)?.split(" ")
        return listOfStringGameItem ?: listOf()
    }

    override suspend fun addOwnership(ownership: Ownership) {
        dataRepository.postOwnership(ownership)
    }

    override suspend fun getOwnership(username: String, itemId: String): List<String> {
        val listOfOwnership = dataRepository.getOwnership(username, itemId).value
        return listOfOwnership ?: listOf()
    }
}