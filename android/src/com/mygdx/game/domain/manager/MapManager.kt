package com.mygdx.game.domain.manager

import android.content.Context
import android.location.Location
import com.mygdx.game.data.dao.Item
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Route

interface MapManager{
    suspend fun getUserLocation(player: Player, context: Context): Location?
    suspend fun getNearbyPlayers(userLocation: Location): List<Player>
    suspend fun getRoute(from:Location,to:Location): Route
    suspend fun getNearbyObjects(userLocation: Location): List<Item>
    suspend fun updateItemsLocation(userLocation: Location): List<Item>
    suspend fun updatePlayersLocation(userLocation: Location): List<Player>
    fun startLocUpdates()
    fun stopLocUpdates()
}