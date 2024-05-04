package com.mygdx.game.domain.usecase.appEntry

import com.mygdx.game.domain.manager.LocalUserManager

class SaveAppEntry(
    private val localUserManager: LocalUserManager
){
    suspend operator fun invoke(){
        localUserManager.saveAppEntry()
    }
}