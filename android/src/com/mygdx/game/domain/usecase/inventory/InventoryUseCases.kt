package com.mygdx.game.domain.usecase.inventory

import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.ar.AddGameItem
import com.mygdx.game.domain.usecase.map.ReadOldGameItems

data class InventoryUseCases (
    val getGameItemsUser: GetGameItemsUser,
    val fetchUserProfile: ReadUser,
    val deleteGameItem: DeleteGameItem,
    val saveOldItems : SaveOldItems,
    val mergeItem: MergeItems,
    val readOldGameItems: ReadOldGameItems
)