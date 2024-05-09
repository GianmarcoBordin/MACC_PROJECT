package com.mygdx.game.domain.usecase.map

import com.mygdx.game.domain.usecase.Subscribe

data class MapUseCases (
    val fetchUserLocation: FetchUserLocation,
    val updatePlayerLocation: UpdatePlayerLocation,
    val updateItemLocation: UpdateItemLocation,
    val getNearbyPlayers : GetNearbyPlayers,
    val getRoute: GetRoute,
    val getNearbyObjects: GetNearbyObjects,
    val startLocUpdates: StartLocUpdates,
    val stopLocUpdates: StopLocUpdates,
    val getContext : GetContext,
    val saveGameItem: SaveGameItem,
    val updateUserLocation: UpdateUserLocation,
    val subscribe: Subscribe,
)