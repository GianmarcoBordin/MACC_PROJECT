package com.mygdx.game.domain.usecase.map

import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.util.Constants

class SaveSeason(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke(season:String) {
        return localUserManager.saveSeason(season)
    }

}