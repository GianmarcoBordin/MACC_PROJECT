package com.mygdx.game.domain.usecase.inventory

import androidx.lifecycle.LiveData
import com.mygdx.game.domain.manager.ARManager
import com.mygdx.game.domain.manager.InventoryManager


class GetGameItemsUser(
    private val inventoryManager: InventoryManager
) {
    suspend operator fun invoke(username: String): LiveData<List<String>> {
        return inventoryManager.getGameItemsUser(username)
    }
}