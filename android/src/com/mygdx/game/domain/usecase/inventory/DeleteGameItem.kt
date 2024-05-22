package com.mygdx.game.domain.usecase.inventory

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.InventoryManager

class DeleteGameItem(
    private val inventoryManager: InventoryManager,
) {
    suspend operator fun invoke(gameItem: GameItem): LiveData<String> {
        return inventoryManager.deleteItems(gameItem)
    }
}