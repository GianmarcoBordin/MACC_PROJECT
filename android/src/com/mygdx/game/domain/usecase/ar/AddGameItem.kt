package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.ARManager

class AddGameItem(
    private val arManager: ARManager
) {
    suspend operator fun invoke(gameItem: GameItem) {
        arManager.addGameItem(gameItem)
    }
}