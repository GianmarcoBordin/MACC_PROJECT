package com.mygdx.game.domain.usecase.ar

import com.mygdx.game.data.dao.Ownership
import com.mygdx.game.domain.manager.ARManager

class AddOwnership(
    private val arManager: ARManager
) {
    suspend operator fun invoke(ownership: Ownership) {
        arManager.addOwnership(ownership)
    }
}