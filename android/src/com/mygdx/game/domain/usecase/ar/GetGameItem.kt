package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.domain.manager.ARManager

class GetGameItem(
    private val arManager: ARManager
) {
    suspend operator fun invoke(username: String, rarity: String): List<String> {
        return arManager.getGameItem(username, rarity)
    }
}