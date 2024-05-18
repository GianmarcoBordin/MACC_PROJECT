package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.util.Constants.OWNERSHIPS


class SaveObject(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke(caughtObjects: MutableList<Boolean>) {
        return localUserManager.saveObject(OWNERSHIPS,caughtObjects)
    }

}