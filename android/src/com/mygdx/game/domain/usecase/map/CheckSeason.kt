package com.mygdx.game.domain.usecase.map

import android.content.Context
import com.mygdx.game.domain.manager.ContextManager
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.util.Constants

class CheckSeason(
    private val localUserManager: LocalUserManager
) {
    operator fun invoke(): String {
        return localUserManager.getSeason()
    }

}