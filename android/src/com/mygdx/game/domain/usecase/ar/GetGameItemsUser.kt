package com.mygdx.game.domain.usecase.ar

import androidx.lifecycle.LiveData
import com.mygdx.game.domain.manager.ARManager


class GetGameItemsUser(
    private val arManager: ARManager
) {
    suspend operator fun invoke(username: String): LiveData<List<String>> {
        return arManager.getGameItemsUser(username)
    }
}