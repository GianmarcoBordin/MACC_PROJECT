package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.map.GetObject

data class ARUseCases (
    val addGameItem: AddGameItem,
    val getGameItem: GetGameItem,
    val fetchUserProfile: ReadUser,
    val saveObject: SaveObject,
    val getObject: GetObject
)