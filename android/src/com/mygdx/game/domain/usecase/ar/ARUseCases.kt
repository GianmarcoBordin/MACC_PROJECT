package com.mygdx.game.domain.usecase.ar

data class ARUseCases (
    val addGameItem: AddGameItem,
    val getGameItem: GetGameItem,
    val addOwnership: AddOwnership,
    val getOwnership: GetOwnership
)