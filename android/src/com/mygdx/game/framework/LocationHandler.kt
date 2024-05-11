@file:Suppress("DEPRECATION")

package com.mygdx.game.framework

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.mygdx.game.util.Constants

class LocationHandler(private val context: Context?) {

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

     fun requestLocationUpdates(callback: (Location) -> Unit) {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                callback(location)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }

        try {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                locationListener!!
            )

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        locationListener?.let { locationManager?.removeUpdates(it) }
    }

    companion object {
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1 // 1 minute
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 20 // 20 meters
    }

    fun getUserLocation(context: Context): Location? {
        // Initialize location manager
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get last known location
            val locationProvider = locationManager.getBestProvider(Criteria(), false)

            // Check if locationProvider is null
            return if (locationProvider != null) {
                Log.d(ContentValues.TAG,"Returning the user location")
                locationManager.getLastKnownLocation(locationProvider)
            } else {
                // Handle case where location provider is null
                Log.d(ContentValues.TAG,"Location provider is null")
                locationManager.getLastKnownLocation(Constants.DEFAULT_PROVIDER_NAME)

            }
        } else {
            // You can't return location here because the permission is requested asynchronously
            Log.d(ContentValues.TAG,"No permission to do this")
            return null
        }
    }



}

