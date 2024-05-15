@file:Suppress("DEPRECATION")

package com.mygdx.game.data.api

import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mygdx.game.data.dao.Item
import com.mygdx.game.data.dao.Map
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Route
import com.mygdx.game.domain.api.MapApi
import macc.ar.domain.api.MapRepository
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.test.GeoPoint
import com.mygdx.game.util.Constants.DEFAULT_LATITUDE
import com.mygdx.game.util.Constants.DEFAULT_LONGITUDE
import com.mygdx.game.util.Constants.LAST_STORED_TIME_KEY
import com.mygdx.game.util.Constants.MINIMUM_TIME_BETWEEN_LOCATION_UPDATES
import com.mygdx.game.util.Constants.RADIUS
import com.mygdx.game.util.Constants.RADIUS2
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MapRepositoryImpl(
    private val mapApi: MapApi?,
    private val firestore: FirebaseFirestore?,
    private val localUserManager: LocalUserManager
): MapRepository {

    override suspend fun getUserLocation(player: Player, context: Context): Location? {
        val userLocation = localUserManager.readLocation()
        if (userLocation != null) {
            val lastStoredTime = getLastStoredTimeFromPreferences(context)
            val currentTime = Date().time
            val timeDifference = currentTime - lastStoredTime
            // Check if enough time has passed since the last location storage
            val minimumTimeDifference = MINIMUM_TIME_BETWEEN_LOCATION_UPDATES // Set your minimum time here, e.g., 1 hour
            if (timeDifference >= minimumTimeDifference) {
                // Store the user's location in Firestore
                val updatePlayer= Player(
                    username = player.username,
                    location = userLocation,
                    distance = 0.0
                )
                // Update last stored time
                saveLastStoredTimeToPreferences(context, currentTime)
                // update remote player setting
                postPlayerLocation(updatePlayer) { isSuccess ->
                    if (isSuccess) {
                        // The operation was successful
                        Log.d("MAP_REPO","Player location added successfully")
                    } else {
                        // The operation failed
                        Log.d("MAP_REPO","Failed to add player location")
                    }
                }
            }
        }

        return userLocation
    }
    override suspend fun updatePlayersLocation(userLocation: Location): List<Player> {
        return getNearbyPlayers(userLocation)
    }

    override suspend fun updateItemsLocation(userLocation: Location): List<Item> {
       return getNearbyObjects(userLocation)
    }

    override suspend fun getNearbyPlayers(userLocation: Location): List<Player> {
        return suspendCoroutine { continuation ->
            // Call queryPlayersLocation() function to get list of players
            queryPlayersLocation(
                { players ->
                    // Resume the coroutine with the list of players
                    continuation.resume(players)
                },userLocation
            )
        }
    }
    override suspend fun getNearbyObjects(userLocation: Location): List<Item> {
        return suspendCoroutine { continuation ->
            // Call queryPlayersLocation() function to get list of players
            queryItemsLocation(userLocation) { items ->
                continuation.resume(items)
            }
        }
    }
    override suspend fun getRoute(from: Location, to: Location): Route {
        val apiKey = "5b3ce3597851110001cf62487ba938b5799c43f9b3e02eb1a95b62ee"
        val startPointStr = "${from.latitude},${from.longitude}"
        val endPointStr = "${to.latitude},${to.longitude}"
        // only 350 meters radius in foot
        Log.d("MAP REPOSITORY","From: $startPointStr    $endPointStr")
        return try {
            val response = mapApi?.calculateNavigationPath(startPointStr, endPointStr, apiKey)
            if (response != null && response.isSuccessful) {
                val jsonString = response.body()?.toString()
                var jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
                jsonObject=extractGeometry(jsonObject)
                // Check if the response contains the expected key
                if (jsonObject.has("coordinates")) {
                    val routePoints = extractGeometryPoints(jsonObject,false)
                    Route(routePoints)
                } else {
                    // Handle missing key error
                    Log.d("MAP REPOSITORY","Error: Missing expected key")
                    Route(emptyList())
                }
            } else {
                // Handle error

                val errorResponse = response?.errorBody()?.string() ?: "Unknown error"
                Log.d("MAP REPOSITORY","Error response: $errorResponse")
                postRoute(from, to)
            }
        } catch (e: Exception) {
            // Handle exception
            Log.d("MAP REPOSITORY","Exception: ${e.message}")
            postRoute(from, to)
        }
    }
    private suspend fun postRoute(from: Location, to: Location): Route {
        val apiKey = "5b3ce3597851110001cf62487ba938b5799c43f9b3e02eb1a95b62ee"
        // more than 2000 meters radius in foot

        val coordinates = listOf(
            listOf(from.longitude, from.latitude),
            listOf(to.longitude, to.latitude)
        )

        val radiuses = listOf(RADIUS)

        val requestBody = Map(coordinates,radiuses)

        return try {
            val response = mapApi?.calculateNavigationPathPost(requestBody, apiKey)
            // Handle the response and extract the route points
            // Return the Route object
            if (response != null && response.isSuccessful) {
                val jsonString = response.body()?.toString()

                var jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
                jsonObject=extractGeometry(jsonObject)

                // Check if the response contains the expected key
                if (jsonObject.has("coordinates")) {
                    val routePoints = extractGeometryPoints(jsonObject,true)
                    Route(routePoints)
                } else {
                    // Handle missing key error
                    println("Error: Missing expected key")
                    Route(emptyList())
                }
            } else {
                // Handle error
                val errorResponse = response?.errorBody()?.string() ?: "Unknown error"
                println("Error response: $errorResponse")
                Route(emptyList())
            }
        } catch (e: Exception) {
            // Handle exceptions
            println("Exception: ${e.message}")
            Route(emptyList())
        }
    }
    override fun startLocUpdates() {
        localUserManager.startLocUpdates()
    }
    override fun stopLocUpdates() {
        localUserManager.stopLocUpdates()
    }
    private fun extractGeometry(jsonObj: JsonObject): JsonObject? {
        // Check if the input JSON object contains the "features" property
        if (jsonObj.has("features")) {
            // Get the "features" array from the JSON object
            val featuresArray = jsonObj.getAsJsonArray("features")

            // Iterate through the features array to find the one with the "geometry" property
            for (feature in featuresArray) {
                val featureObj = feature as JsonObject
                if (featureObj.has("geometry")) {
                    return featureObj.getAsJsonObject("geometry")
                }
            }
        }

        return null
    }
    private fun extractGeometryPoints(jsonObj: JsonObject,switch:Boolean): List<org.osmdroid.util.GeoPoint> {
        val geometryPoints = mutableListOf<org.osmdroid.util.GeoPoint>()

        // Check if the "geometry" object is not null
        // Get the "coordinates" array from the "geometry" object

        // Check if the "coordinates" array is not null
        for (coordinate in jsonObj.getAsJsonArray("coordinates")) {
            // Parse the coordinates into latitude and longitude
            val coordinates = coordinate as JsonArray
            var longitude: Double
            var latitude: Double
            if (switch) {
                longitude = coordinates[0].asDouble
                latitude = coordinates[1].asDouble
            }else{
                latitude = coordinates[0].asDouble
                longitude = coordinates[1].asDouble
            }
            // Create a GeoPoint object and add it to the list of geometry points
            geometryPoints.add(org.osmdroid.util.GeoPoint(latitude, longitude))
        }

        return geometryPoints
    }
    private fun queryPlayersLocation(callback: (List<Player>) -> Unit, userLocation: Location) {
        // Query nearby locations within a certain radius

        val radius = RADIUS2  // Radius in meters
        val defaultLocationGeoPoint =
            com.google.firebase.firestore.GeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)

        val query = firestore?.collection("players")

        query?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val playersList = mutableListOf<Player>()
                    for (document in task.result!!) {
                        val username = document.getString("username") ?: ""
                        val locationGeoPoint = document.getGeoPoint("location") ?: defaultLocationGeoPoint
                        // conversion
                        val location = Location("provider")
                        location.latitude = locationGeoPoint.latitude
                        location.longitude = locationGeoPoint.longitude
                        val distance = calculateDistance(userLocation,location)
                        if (distance < radius) {
                            val player = Player(username, location, distance)
                            playersList.add(player)
                        }
                    }
                    Log.w(TAG, "Success getting documents. $playersList")
                    callback(playersList)
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    callback(emptyList()) // Callback with empty list in case of error
                }
            }
    }
    private fun postPlayerLocation(player: Player, callback: (Boolean) -> Unit) {
        // Create a new document with a generated ID
        val document = firestore?.collection("players")?.document(player.username)

        // Create a map with player data
        val playerData = hashMapOf(
            "username" to player.username,
            "location" to com.google.firebase.firestore.GeoPoint(
                player.location.latitude,
                player.location.longitude
            ),
        )


        document?.set(playerData)
            ?.addOnSuccessListener {
                Log.d(TAG, "Player location added successfully")
                callback(true)
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding player location", e)
                callback(false)
            }
    }

    private fun queryItemsLocation(userLocation: Location, callback: (List<Item>) -> Unit) {
        // Query nearby locations within a certain radius

        val radius = RADIUS2  // Radius in meters
        val defaultLocationGeoPoint =
            com.google.firebase.firestore.GeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)

        val query = firestore?.collection("items")

        query?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val objectsList = mutableListOf<Item>()
                    for (document in task.result!!) {
                        val itemId = document.get("itemId") ?: 0
                        val itemRarity = document.getString("itemRarity") ?: ""
                        val locationGeoPoint = document.getGeoPoint("location") ?: defaultLocationGeoPoint
                        // conversion
                        val location = Location("provider")
                        location.latitude = locationGeoPoint.latitude
                        location.longitude = locationGeoPoint.longitude
                        val distance = calculateDistance(userLocation,location)
                        if (distance < radius) {
                            val obj = Item(itemId.toString(), itemRarity, distance, location)
                            objectsList.add(obj)
                        }
                    }
                    callback(objectsList)
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    callback(emptyList()) // Callback with empty list in case of error
                }
            }
    }

    private fun calculateDistance(location1: Location, location2: Location): Double {
        val earthRadius = 6371 // Earth's radius in kilometers

        val thresholdKm = 1000 // Set the threshold to 1 kilometer

        val lat1 = Math.toRadians(location1.latitude)
        val lon1 = Math.toRadians(location1.longitude)
        val lat2 = Math.toRadians(location2.latitude)
        val lon2 = Math.toRadians(location2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceInKm = earthRadius * c // Distance in kilometers

        val distanceString = if (distanceInKm > thresholdKm) {
            "%.2f km".format(distanceInKm)
        } else {
            val distanceInMeters = distanceInKm * 1000 // Convert kilometers to meters
            "%.0f meters".format(distanceInMeters)
        }

        // Extract numerical part from the distance string and transform to double

        return distanceString.replace(Regex("[^0-9.]"), "").toDouble()
    }

    private fun getLastStoredTimeFromPreferences(context: Context): Long {
        // Retrieve the last stored time from SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getLong(LAST_STORED_TIME_KEY, 0)
    }

    private fun saveLastStoredTimeToPreferences(context: Context, lastStoredTime: Long) {
        // Save the last stored time to SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putLong(LAST_STORED_TIME_KEY, lastStoredTime)
        editor.apply()
    }
}