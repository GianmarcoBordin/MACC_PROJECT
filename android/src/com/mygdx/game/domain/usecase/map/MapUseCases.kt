package com.mygdx.game.domain.usecase.map

import com.mygdx.game.domain.usecase.Subscribe
import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.inventory.GetGameItemsUser
import com.mygdx.game.domain.usecase.inventory.SaveOldItems

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
    val readOldGameItems : ReadOldGameItems,
    val saveOldItems: SaveOldItems,
    val checkSeason: CheckSeason,
    val saveSeason: SaveSeason
)