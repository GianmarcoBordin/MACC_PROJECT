package com.mygdx.game.presentation.map


import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mygdx.game.data.dao.GameItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import com.mygdx.game.data.dao.Item
import com.mygdx.game.data.dao.Message
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.manager.UpdateListener
import com.mygdx.game.domain.manager.ContextManager
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.usecase.appEntry.AppEntryUseCases
import com.mygdx.game.domain.usecase.map.MapUseCases
import com.mygdx.game.presentation.map.events.LocationDeniedEvent
import macc.ar.presentation.map.events.LocationGrantedEvent
import com.mygdx.game.presentation.map.events.RetryMapEvent
import com.mygdx.game.presentation.map.events.RouteEvent
import com.mygdx.game.presentation.map.events.UpdateMapEvent
import com.mygdx.game.util.Constants
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class MapViewModel  @Inject constructor(
    private val mapUseCases: MapUseCases,
    private val contextManager: ContextManager,
    private val appEntryUseCases: AppEntryUseCases,
    private val localUserManager: LocalUserManager
): ViewModel(), UpdateListener {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isLocGranted = MutableLiveData<Boolean>()
    val isLocGranted: LiveData<Boolean> = _isLocGranted

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation

    private val _players = MutableLiveData<List<Player>?>()
    val players: LiveData<List<Player>?> = _players

    private val _objects= MutableLiveData<List<Item>?>()
    val objects: LiveData<List<Item>?> = _objects

    private val _navPath = MutableLiveData<List<GeoPoint>?>()
    val navPath: LiveData<List<GeoPoint>?> = _navPath

    private val _thresholdButtonFlag = MutableLiveData<HashMap<Item, Boolean>?>()
    val thresholdButtonFlag: LiveData<HashMap<Item, Boolean>?> = _thresholdButtonFlag

    private val _to = MutableLiveData<Location?>()

    private val isActive = MutableLiveData<Boolean>()



    init {
        // Set granting state
        _isLocGranted.value=false
        mapUseCases.subscribe.invoke(this, Constants.MAP)
        viewModelScope.launch {
            periodicExecution(3)
        }
    }
    override fun onCleared() {
        super.onCleared()
        release()

    }

     fun update(item:Item,value:Boolean) {
          val _map = HashMap<Item, Boolean>()
         _map.set(item,value)
        _thresholdButtonFlag.value=_map
         Log.d("FUC>K","${_thresholdButtonFlag.value}")
    }

     fun saveGameItem(gameItem: GameItem){
         viewModelScope.launch {
             localUserManager.saveGameItem(gameItem)
         }
    }


    override fun onUpdate(data: Location) { // receives updates from data layer
        _userLocation.value=data
        // when I rx an update from data layer the position is changed so if I have a route in progress I will update it
        if (_navPath.value!=null){
            goRouting(_userLocation.value,_to.value ?: Location("provider"))
        }
    }

    override fun onUpdate(data: String) {
        // not used
    }

    override fun onUpdate(data: Message) {
        //
    }

    private fun fetchData(context: Context) {
        startLocationUpdates()
        viewModelScope.launch {
            var userLoc = Location("provider")
            var ps: List<Player>? = null
            var objs: List<Item>? = null
            // Set loading state
            _isLoading.value = true
            _isError.value = false
            delay(1000)
            runCatching {
                // Fetch user location
                val player = Player(
                    username = appEntryUseCases.readUser().displayName,
                    location = userLoc,
                    distance = 0.0
                )
                userLoc = mapUseCases.fetchUserLocation(player, context)
                    ?: throw IllegalStateException("User location is null")

                // Fetch nearby players
                ps = mapUseCases.getNearbyPlayers(userLoc)


                // Fetch nearby objects
                objs = mapUseCases.getNearbyObjects(userLoc)


            }.onFailure { e ->
                // Handle error only if user location is null
                Log.d(TAG, "Error fetching data: ${e.message}")
                _isLoading.value = false
                stopLocationUpdates()
            }.onSuccess {
                Log.d(TAG, "Success fetching data, players: $ps Objects: $objs")

                // Update LiveData
                _userLocation.value = userLoc
                _players.value = ps ?: emptyList()
                _objects.value = objs ?: emptyList()
                // Set loading state to false
                _isLoading.value = false
            }
        }
    }
    fun onMapRetryEvent(event: RetryMapEvent) {
        when (event) {
            is RetryMapEvent.MapRetry -> {
                goMapRetry()
            }

        }
    }
    fun onMapUpdateEvent(event: UpdateMapEvent) {
        when (event) {
            is UpdateMapEvent.MapUpdate -> {
                goMapUpdate()
            }

        }
    }
    fun onRouteEvent(event: RouteEvent) {
        when (event) {
            is RouteEvent.Route -> {
                goRouting(event.from,event.to)
            }

        }
    }
    private fun goRouting(from:  Location?, to: Location) {
        _to.value=to
        viewModelScope.launch {
            val route = from?.let { mapUseCases.getRoute(it, to) }
                ?: throw IllegalStateException("Route is null")

            // Map route points to GeoPoints
            val navPath = route.points.map { location ->
                GeoPoint(location.latitude, location.longitude)
            }
            _navPath.value = navPath
        }
    }
    fun onLocationGrantedEvent(event: LocationGrantedEvent) {
        when (event) {
            is LocationGrantedEvent.LocationGranted -> {
                Log.d(TAG,"Location permission granted")
                fetchData(contextManager.getContext())
                _isLocGranted.value=true
            }

        }
    }
    fun onLocationDeniedEvent(event: LocationDeniedEvent) {
        when (event) {
            is LocationDeniedEvent.LocationDenied -> {
                Log.d(TAG,"Location permission denied")
                _isLocGranted.value=false
            }

        }
    }
    private fun goMapUpdate(){
        release()
        fetchData(context =contextManager.getContext() )
       }
    private fun goMapRetry(){
        release()
        fetchData(context=contextManager.getContext())
   }
    fun release() {
        _userLocation.value = null
        _players.value = null
        _objects.value=null
        _navPath.value=null
        isActive.value=false
        _thresholdButtonFlag.value=null
        _to.value=null
        stopLocationUpdates()
    }
    fun resume() {
        isActive.value=true
        startLocationUpdates()
        fetchData(contextManager.getContext())
    }
     private fun startLocationUpdates() {
        mapUseCases.startLocUpdates()
    }
     private fun stopLocationUpdates() {
        mapUseCases.stopLocUpdates()
    }

    private fun periodicExecution(intervalMinutes: Long) = flow<Unit> {
        val usrLoc=_userLocation.value ?: Location("provider")
        // Global variable to hold the job
        val playersLocationJob: Job? = null
        val itemsLocationJob: Job? = null
            while (isActive.value == true) {
                _players.value =
                    mapUseCases.updatePlayerLocation(usrLoc)
                _objects.value = mapUseCases.updateItemLocation(usrLoc)
                delay(TimeUnit.MINUTES.toMillis(intervalMinutes)) // Wait for the specified interval
            }
        playersLocationJob?.cancel()
        itemsLocationJob?.cancel()
    }
}
