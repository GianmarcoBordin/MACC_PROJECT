package com.mygdx.game.domain.usecase.map

import android.location.Location
import com.mygdx.game.data.dao.Item
import com.mygdx.game.domain.manager.MapManager


class UpdateItemLocation(
    private val mapManager: MapManager
){
    suspend operator fun invoke(userLocation: Location): List<Item> {
        return mapManager.updateItemsLocation(userLocation)
    }

}