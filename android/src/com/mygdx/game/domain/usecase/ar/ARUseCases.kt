package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.domain.usecase.appEntry.ReadUser

data class ARUseCases (
    val addGameItem: AddGameItem,
    val getGameItem: GetGameItem,
    val fetchUserProfile: ReadUser,
)