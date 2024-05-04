package com.mygdx.game.domain.usecase

import com.mygdx.game.data.manager.UpdateListener
import com.mygdx.game.domain.manager.AuthManager
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.manager.RankManager
import com.mygdx.game.domain.manager.SettingsManager
import com.mygdx.game.util.Constants.MAP
import com.mygdx.game.util.Constants.MP
import com.mygdx.game.util.Constants.USER_AUTH
import com.mygdx.game.util.Constants.USER_RANK
import com.mygdx.game.util.Constants.USER_SETTINGS


class Subscribe(
    private val settingsManager: SettingsManager,
    private val authManager: AuthManager,
    private val rankManager: RankManager,
    private val localUserManager: LocalUserManager

){
    operator fun invoke(ref: UpdateListener, type:String){
        when (type) {
            USER_AUTH ->  authManager.setUpdateListener(ref)
            USER_RANK -> rankManager.setUpdateListener(ref)
            USER_SETTINGS -> settingsManager.setUpdateListener(ref)
            MAP -> localUserManager.setUpdateListener(ref)
        }
    }

}