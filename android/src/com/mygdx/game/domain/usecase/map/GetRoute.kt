package com.mygdx.game.domain.usecase.map

import android.location.Location
import com.mygdx.game.data.dao.Route
import com.mygdx.game.domain.manager.MapManager


class GetRoute(
    private val mapManager: MapManager
) {
    suspend operator fun invoke(from: Location, to: Location): Route {
        return mapManager.getRoute(from, to)
    }

}