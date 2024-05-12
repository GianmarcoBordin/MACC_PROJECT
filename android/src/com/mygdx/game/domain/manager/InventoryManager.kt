package com.mygdx.game.domain.manager

import com.mygdx.game.data.dao.GameItem

interface InventoryManager {
    suspend fun retrieveItems(username: String) : List<List<String>>
}