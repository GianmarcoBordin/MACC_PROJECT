package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.settings.FetchUserProfile

data class ARUseCases (
    val addGameItem: AddGameItem,
    val getGameItem: GetGameItem,
    val getGameItemsUser: GetGameItemsUser,
    val addOwnership: AddOwnership,
    val getOwnership: GetOwnership,
    val readGameItem: ReadGameItem,
    val fetchUserProfile: ReadUser,
)