package com.mygdx.game.domain.usecase.map

import com.mygdx.game.domain.manager.MapManager

class StopLocUpdates(
    private val mapManager: MapManager
) {
    operator fun invoke() {
        return mapManager.stopLocUpdates()
    }

}