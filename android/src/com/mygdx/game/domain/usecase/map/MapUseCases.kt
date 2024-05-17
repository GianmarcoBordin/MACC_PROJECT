package com.mygdx.game.domain.usecase.map

import com.mygdx.game.domain.usecase.Subscribe
import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.ar.SaveObject
import com.mygdx.game.domain.usecase.inventory.GetGameItemsUser

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
    val updateUserLocation: UpdateUserLocation,
    val subscribe: Subscribe,
    val readUser: ReadUser,
    val getGameItemsUser: GetGameItemsUser,
    val getObject : GetObject,
    val saveObject: SaveObject
)