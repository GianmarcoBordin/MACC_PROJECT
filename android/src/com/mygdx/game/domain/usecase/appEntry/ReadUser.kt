package com.mygdx.game.domain.usecase.appEntry

import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.manager.LocalUserManager

class ReadUser(
    private val localUserManager: LocalUserManager
){
     operator fun invoke(): UserProfileBundle {
        return localUserManager.getUserProfile()
    }
}