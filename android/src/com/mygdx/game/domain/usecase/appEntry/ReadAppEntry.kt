package com.mygdx.game.domain.usecase.appEntry

import com.mygdx.game.domain.manager.LocalUserManager

class ReadAppEntry(
    private val localUserManager: LocalUserManager
){
     operator fun invoke(): List<Boolean> {
        return localUserManager.readAppEntry()
    }
}