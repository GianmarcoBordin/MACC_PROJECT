package com.mygdx.game.domain.usecase.ar

data class ARUseCases (
    val addGameItem: AddGameItem,
    val getGameItem: GetGameItem,
    val getGameItemsUser: GetGameItemsUser,
    val addOwnership: AddOwnership,
    val getOwnership: GetOwnership
)