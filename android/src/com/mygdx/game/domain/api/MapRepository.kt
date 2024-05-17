package macc.ar.domain.api

import android.content.Context
import android.location.Location
import com.mygdx.game.data.dao.Item
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Route

interface MapRepository {
    suspend fun getUserLocation(player: Player, context: Context): Location?
    suspend fun getNearbyPlayers(userLocation: Location): List<Player>
    suspend fun getRoute(from: Location, to: Location): Route

     fun startLocUpdates()

     fun stopLocUpdates()

    suspend fun getNearbyObjects(userLocation: Location): MutableList<Item>
    suspend fun updateItemsLocation(userLocation: Location): List<Item>
    suspend fun updatePlayersLocation(userLocation: Location): List<Player>
}