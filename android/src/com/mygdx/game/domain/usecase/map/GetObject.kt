package com.mygdx.game.domain.usecase.map

import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.util.Constants

class GetObject(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke(): List<String> {
        return localUserManager.getObjectList(Constants.OWNERSHIPS)
    }

}