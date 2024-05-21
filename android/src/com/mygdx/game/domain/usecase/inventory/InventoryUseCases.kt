package com.mygdx.game.domain.usecase.inventory

import com.mygdx.game.domain.usecase.appEntry.ReadUser

data class InventoryUseCases (
    val getGameItemsUser: GetGameItemsUser,
    val fetchUserProfile: ReadUser,
    val mergeItems: MergeItems,
)