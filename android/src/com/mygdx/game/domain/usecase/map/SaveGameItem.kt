package com.mygdx.game.domain.usecase.map

import android.content.Context
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.LocalUserManager


class SaveGameItem(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke(gameItem: GameItem) {
         localUserManager.saveGameItem(gameItem)
    }

}