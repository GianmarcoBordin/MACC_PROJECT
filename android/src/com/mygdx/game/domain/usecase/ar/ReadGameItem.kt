package com.mygdx.game.domain.usecase.ar

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.ARManager
import com.mygdx.game.domain.manager.LocalUserManager


class ReadGameItem(
    private val localUserManager: LocalUserManager
) {
    operator fun invoke(): GameItem {
        return localUserManager.readGameItem()
    }
}