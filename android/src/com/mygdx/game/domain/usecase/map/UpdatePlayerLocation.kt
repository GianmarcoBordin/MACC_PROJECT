package com.mygdx.game.domain.usecase.map

import android.location.Location
import com.mygdx.game.data.dao.Player
import com.mygdx.game.domain.manager.MapManager


class UpdatePlayerLocation(
    private val mapManager: MapManager
){
    suspend operator fun invoke(userLocation: Location): List<Player> {
        return mapManager.updatePlayersLocation(userLocation)
    }

}