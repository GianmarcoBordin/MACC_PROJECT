package com.mygdx.game.domain.usecase.appEntry

data class AppEntryUseCases (
    val readAppEntry: ReadAppEntry,
    val saveAppEntry: SaveAppEntry,
    val readUser: ReadUser,
    val saveUser: SaveUser
)