package com.mygdx.game.domain.manager

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem

interface ARManager {
    suspend fun addGameItem(gameItem: GameItem)
    suspend fun getGameItem(username: String, rarity: String): LiveData<List<String>>

    suspend fun saveSkin()
}