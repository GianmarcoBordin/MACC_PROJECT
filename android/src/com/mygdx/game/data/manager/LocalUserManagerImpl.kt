package com.mygdx.game.data.manager

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.mygdx.game.data.dao.Biometric
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.framework.LocationHandler
import com.mygdx.game.framework.postPlayerToFirestore
import com.mygdx.game.framework.retrieveFirebaseUserIdAndBiometricCredentials
import com.mygdx.game.framework.saveFirebaseUserIdAndBiometricCredentials
import com.mygdx.game.util.Constants
import com.mygdx.game.util.Constants.USER_SETTINGS
import com.mygdx.game.util.Constants.USER_SETTINGS2

/*
* Implementation of user manager, it implements method to store user preferences on a datastore,
* accessible from the application context*/
class LocalUserManagerImpl(
    private val context: Context,
    private val locationHandler: LocationHandler, private val firestore: FirebaseFirestore
): LocalUserManager {

    private var updateListener: UpdateListener? = null
    val dataScope = CoroutineScope(Dispatchers.IO)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(USER_SETTINGS2, Context.MODE_PRIVATE)
    private val gson = Gson()



    override fun saveObject(key: String, item: Any) {
        val jsonString = gson.toJson(item)
        sharedPreferences.edit().putString(key, jsonString).apply()
    }

    override fun getObject(key: String): String {
        val jsonString = sharedPreferences.getString(key, null)
        return gson.fromJson(jsonString, String::class.java)
    }



    override suspend fun saveAppEntry() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.APP_ENTRY] = true
        }
    }


    override fun readAppEntry(): List<Boolean> {
        val result = mutableListOf<Boolean>()
        val data = context.dataStore.data
        val preferences = runBlocking { data.first() } // Blocking operation to get the first emission
        result.add(preferences[PreferencesKeys.APP_ENTRY] ?: false)

        return result
    }

    override suspend fun clearAppEntry() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.APP_ENTRY] = false
        }
    }

    override suspend fun saveAnchorId(anchorId: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.CLOUD_ANCHOR_ID] = anchorId
        }
    }

    override fun readAnchorId(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.CLOUD_ANCHOR_ID] ?: ""
        }
    }

    override suspend fun saveBio(biometric: Biometric) {
        saveFirebaseUserIdAndBiometricCredentials(context,biometric)
    }

    override fun readBio(): Pair<String, String> {
        return  retrieveFirebaseUserIdAndBiometricCredentials(context)

    }

    override suspend fun saveScore(rank: Rank) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.SCORE] = rank.score.toString()
        }
    }

    override fun readScore(): Rank {
        val data = context.dataStore.data
        val preferences = runBlocking { data.first() } // Blocking operation to get the first emission
        val displayName = preferences[PreferencesKeys.DISPLAY_NAME] ?: ""
        val score= preferences[PreferencesKeys.SCORE] ?: "0"
        return Rank(displayName,score.toInt())
    }
    override fun readLocation(): Location? {
        return locationHandler.getUserLocation(context)
    }

    override fun startLocUpdates() {
        locationHandler.requestLocationUpdates { location ->
            updateListener?.onUpdate(location)
            val userProfileBundle=getUserProfile()
            dataScope.launch {
                val userPlayer= Player(userProfileBundle.displayName, location, 0.0)
                postPlayerToFirestore(firestore,userPlayer) }
        }
    }

    override fun stopLocUpdates() {
        locationHandler.stopLocationUpdates()
    }

    override fun getUserProfile(): UserProfileBundle {
        val data = context.dataStore.data
        val preferences = runBlocking { data.first() } // Blocking operation to get the first emission
        val displayName = preferences[PreferencesKeys.DISPLAY_NAME] ?: ""
        val email = preferences[PreferencesKeys.EMAIL] ?: ""
        return UserProfileBundle(displayName, email)
    }

    override suspend fun saveUserProfile(userProfile: UserProfileBundle) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.DISPLAY_NAME] = userProfile.displayName
            settings[PreferencesKeys.EMAIL] = userProfile.email
        }
        saveObject(Constants.USER,userProfile.email)
        saveObject(Constants.USERNAME,userProfile.displayName)
    }

    override suspend fun saveGameItem(gameItem: GameItem) {
        // serialise tu string
        val jsonString = gameItem.toJson()
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.GAME_ITEM] = jsonString
        }
    }

    override fun readGameItem(): GameItem {
        val data = context.dataStore.data
        val preferences = runBlocking { data.first() } // Blocking operation to get the first emission
        val gameItemString = preferences[PreferencesKeys.GAME_ITEM] ?: ""
        return GameItem.fromJson(gameItemString)
    }

    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref    }


}

// Define a datastore to keep user preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTINGS)

// Define a boolean preferences
private object PreferencesKeys{
    // app entry preferences
    val APP_ENTRY = booleanPreferencesKey(name = Constants.APP_ENTRY)
    // ar entry preferences
    val CLOUD_ANCHOR_ID = stringPreferencesKey(name = Constants.CLOUD_ANCHOR_ID )
    val GAME_ITEM = stringPreferencesKey(name = Constants.GAME_ITEM )

    // auth user
    val DISPLAY_NAME = stringPreferencesKey("display_name")
    val EMAIL = stringPreferencesKey("email")
    // user ranking and player
    val SCORE = stringPreferencesKey("score")

}

