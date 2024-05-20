package com.mygdx.game.data.manager

import android.content.Context
import android.location.Location
import com.mygdx.game.data.dao.Item
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Route
import macc.ar.domain.api.MapRepository
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.manager.MapManager
import com.mygdx.game.util.Constants
import javax.inject.Inject


class MapManagerImpl @Inject constructor(private val mapRepository: MapRepository,private val localUserManager: LocalUserManager) :
    MapManager {


    override suspend fun getUserLocation(player: Player, context: Context): Location {
        val default = Location("provider")
        default.latitude = Constants.DEFAULT_LOCATION_LATITUDE
        default.longitude = Constants.DEFAULT_LOCATION_LONGITUDE
        return mapRepository.getUserLocation(player,context) ?: default
    }

    override suspend fun getNearbyPlayers(userLocation: Location): List<Player> {
        return mapRepository.getNearbyPlayers(userLocation)
    }

    override suspend fun getRoute(from: Location, to: Location): Route {
        return mapRepository.getRoute(from,to)
    }

    override suspend fun getNearbyObjects(userLocation: Location): MutableList<Item> {
        return mapRepository.getNearbyObjects(userLocation)
    }

    override suspend fun updateItemsLocation(userLocation: Location): List<Item> {
        val items = mapRepository.updateItemsLocation(userLocation)
        for (item in items) {
            localUserManager.saveObject(item.itemId.toString(), item)
        }
        return items
    }

    override suspend fun updatePlayersLocation(
        userLocation: Location
    ): List<Player> {
        return mapRepository.updatePlayersLocation(userLocation)
     }

    override fun startLocUpdates() {
        localUserManager.startLocUpdates()
    }

    override fun stopLocUpdates() {
        localUserManager.stopLocUpdates()
    }
}

