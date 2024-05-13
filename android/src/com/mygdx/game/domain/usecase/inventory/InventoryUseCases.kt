package com.mygdx.game.domain.usecase.inventory

import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.ar.GetGameItemsUser

data class InventoryUseCases (
    val getGameItemsUser: GetGameItemsUser,
    val fetchUserProfile: ReadUser
)