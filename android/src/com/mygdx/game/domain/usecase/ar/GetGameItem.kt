package com.mygdx.game.domain.usecase.ar

import androidx.lifecycle.LiveData
import com.mygdx.game.domain.manager.ARManager

class GetGameItem(
    private val arManager: ARManager
) {
    suspend operator fun invoke(username: String, rarity: String): LiveData<List<String>> {
        return arManager.getGameItem(username, rarity)
    }
}