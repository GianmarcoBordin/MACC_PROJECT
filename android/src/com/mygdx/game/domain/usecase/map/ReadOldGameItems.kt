package com.mygdx.game.domain.usecase.map

import android.location.Location
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Route
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.manager.MapManager
import com.mygdx.game.util.Constants

class ReadOldGameItems(
    private val localUserManager: LocalUserManager
) {
     operator fun invoke(): List<String> {
        return localUserManager.getObjectList(Constants.OLD_GAME_ITEMS)
    }

}