package com.mygdx.game.domain.usecase.appEntry

import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.manager.LocalUserManager


class SaveUser(
    private val localUserManager: LocalUserManager
){
    suspend operator fun invoke(userProfileBundle: UserProfileBundle) {
        return localUserManager.saveUserProfile(userProfileBundle)
    }
}