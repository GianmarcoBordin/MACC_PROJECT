package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.domain.usecase.appEntry.ReadUser

data class ARUseCases (
    val addGameItem: AddGameItem,
    val getGameItem: GetGameItem,
    val addOwnership: AddOwnership,
    val getOwnership: GetOwnership,
    val readGameItem: ReadGameItem,
    val fetchUserProfile: ReadUser,
)