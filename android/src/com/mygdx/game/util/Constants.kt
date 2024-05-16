package com.mygdx.game.util

/*
* Object to hold constant in all the application*/
object Constants {

    const val FIRESTORE_ID = "firestore"
    const val LOGIN_SUCCESS = "Login success"
    const val LOGIN_FAILED = "Login Failed"

    const val BIO_AUTH_SUCCESS = "Bio Auth Success"
    const val BIO_AUTH_FAILED = "Bio Auth Failed"
    const val BIO_NOT_AVAILABLE = "Biometric authentication not available"
    const val UPDATE_SUCCESS = "Update success"
    const val SIGN_OUT_SUCCESS = "Sign-out success"
    const val SIGN_UP_SUCCESS = "SignUp Success"


    const val EMAIL_IS_IN_USE = "Email is already in use"

    const val USER_SETTINGS = "userSettings"
    const val USER_SETTINGS2 = "userSettings2"
    const val DEFAULT_PROVIDER_NAME = "gps"

    const val USER = "user"
    const val USERNAME = "username"
    const val PLAYER_LIST = "player_list"
    const val MAP = "map"
    const val RADIUS = 2000
    const val RADIUS2 = 50.0
    const val USER_AUTH = "userAuth"
    const val USER_RANK = "userRank"
    const val APP_ENTRY = "appEntry"
    const val CLOUD_ANCHOR_ID = "anchorId"
    const val LAST_STORED_TIME_KEY = "last_stored_time"
    const val GAME_ITEM = "game_item"
    const val RARITY_1_HP = 1
    const val RARITY_2_HP = 2
    const val RARITY_3_HP = 3
    const val RARITY_4_HP = 4
    const val RARITY_5_HP = 5
    const val RARITY_1_DAMAGE = 1
    const val RARITY_2_DAMAGE = 2
    const val RARITY_3_DAMAGE = 3
    const val RARITY_4_DAMAGE = 4
    const val RARITY_5_DAMAGE = 5
    const val RARITY_1_COLOR = "Green"
    const val RARITY_2_COLOR = "Red"
    const val RARITY_3_COLOR = "Yellow"
    const val RARITY_4_COLOR = "Blue"
    const val RARITY_5_COLOR = "Black"
    const val MINIMUM_TIME_BETWEEN_LOCATION_UPDATES = 60000L // 1 minute in milliseconds
    const val DEFAULT_LATITUDE = 0.0
    const val DEFAULT_LONGITUDE = 0.0
    const val RANK_URL = "https://gianm.pythonanywhere.com"
    const val MAPS_URL = "https://api.openrouteservice.org/v2/directions/driving-car"
    const val MAPS_URL2 = "https://api.openrouteservice.org/v2/directions/driving-car/geojson"

    // Default location of the user if GPS doesn't work correctly
    const val DEFAULT_LOCATION_LATITUDE = 41.89098635680032
    const val DEFAULT_LOCATION_LONGITUDE = 12.503826312635637

    const val MARKER_WIDTH = 50
    const val MARKER_HEIGHT = 50

    const val OBJECT_MARKER_WIDTH = (MARKER_WIDTH / 1.5).toInt()
    const val OBJECT_MARKER_HEIGHT = (MARKER_HEIGHT / 1.5).toInt()

}