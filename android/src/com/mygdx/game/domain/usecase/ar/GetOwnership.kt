package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.domain.manager.ARManager

class GetOwnership(
    private val arManager: ARManager
) {
    suspend operator fun invoke(username: String, itemId: String): List<String> {
        return arManager.getOwnership(username, itemId)
    }
}