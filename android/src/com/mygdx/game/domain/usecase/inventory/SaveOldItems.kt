package com.mygdx.game.domain.usecase.inventory

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.InventoryManager
import com.mygdx.game.domain.manager.LocalUserManager

class SaveOldItems(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke(oldGameItems: List<GameItem>) {
        return localUserManager.saveOldItems(oldGameItems)
    }
}