package com.mygdx.game.domain.usecase.inventory

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.InventoryManager

class MergeItems(
    private val inventoryManager: InventoryManager,
) {
    suspend operator fun invoke(mergedItems: List<GameItem>, itemsToDelete: List<GameItem>) {
        return inventoryManager.mergeItems(mergedItems, itemsToDelete)
    }
}