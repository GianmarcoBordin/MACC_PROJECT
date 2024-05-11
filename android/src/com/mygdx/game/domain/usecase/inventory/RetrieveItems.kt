package com.mygdx.game.domain.usecase.inventory

import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.InventoryManager

class RetrieveItems (
    private val inventoryManager: InventoryManager
) {
    suspend operator fun invoke(username: String) : List<List<String>> {
        return inventoryManager.retrieveItems(username)
    }
}