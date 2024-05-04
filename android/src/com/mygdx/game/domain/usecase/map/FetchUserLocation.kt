package com.mygdx.game.domain.usecase.map

import android.content.Context
import com.mygdx.game.data.dao.Player
import com.mygdx.game.domain.manager.MapManager

class FetchUserLocation(
    private val mapManager: MapManager
){
    suspend operator fun invoke(player: Player, context: Context): android.location.Location? {
        return mapManager.getUserLocation(player,context)
    }

}