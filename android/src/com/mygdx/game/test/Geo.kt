package com.mygdx.game.test


import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GeoPoint(val latitude: Double, val longitude: Double)




fun generateRandomGeoPoints(center: GeoPoint, radius: Double, count: Int): List<GeoPoint> {
    val geoPoints = mutableListOf<GeoPoint>()
    val random = java.util.Random()

    repeat(count) {
        val randomRadius = sqrt(random.nextDouble()) * radius
        val randomAngle = random.nextDouble() * 2 * PI
        val offsetX = randomRadius * cos(randomAngle)
        val offsetY = randomRadius * sin(randomAngle)

        val newLatitude = center.latitude + offsetY / 111.0
        val newLongitude = center.longitude + offsetX / (111.0 * cos(center.latitude * PI / 180))

        geoPoints.add(GeoPoint(newLatitude, newLongitude))
    }

    return geoPoints
}

// Earth radius in kilometers
private const val EARTH_RADIUS = 6371.0

// Convert degrees to radians
private fun degreesToRadians(degrees: Double): Double {
    return degrees * PI / 180.0
}

// Convert radians to degrees
private fun radiansToDegrees(radians: Double): Double {
    return radians * 180.0 / PI
}

// Calculate distance between two points using Haversine formula
private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = degreesToRadians(lat2 - lat1)
    val dLon = degreesToRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(degreesToRadians(lat1)) * cos(degreesToRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS * c
}

// Calculate destination point given distance and bearing from starting point
private fun calculateDestination(lat: Double, lon: Double, distance: Double, bearing: Double): Pair<Double, Double> {
    val lat1 = degreesToRadians(lat)
    val lon1 = degreesToRadians(lon)
    val brng = degreesToRadians(bearing)

    val lat2 = asin(sin(lat1) * cos(distance / EARTH_RADIUS) +
            cos(lat1) * sin(distance / EARTH_RADIUS) * cos(brng))

    val lon2 = lon1 + atan2(sin(brng) * sin(distance / EARTH_RADIUS) * cos(lat1),
        cos(distance / EARTH_RADIUS) - sin(lat1) * sin(lat2))

    return Pair(radiansToDegrees(lat2), radiansToDegrees(lon2))
}

fun main() {/*
    val center = GeoPoint(37.4220936, -122.083922) // New York City coordinates
    val radius = 4.350 // 350 m radius
    val count = 1 // Number of GeoPoints to generate

    val randomGeoPoints = generateRandomGeoPoints(center, radius, count)

    println("Random GeoPoints within 350 m radius from center:")
    randomGeoPoints.forEach { geoPoint ->
        println("Latitude: ${geoPoint.latitude}, Longitude: ${geoPoint.longitude}")
    }*/

    // Starting point coordinates
    val startLat = 37.421998333333335
    val startLon = -122.084

    // Distance to travel in kilometers
    val distance = 2.0

    // Bearing (0 degrees is north)
    val bearing = 0.0

    // Calculate destination point
    val (destLat, destLon) = calculateDestination(startLat, startLon, distance, bearing)

    // Print the coordinates of the destination point
    println("Coordinates of a point more than 2km away: $destLat, $destLon")

}
