package com.mygdx.game.test


import kotlin.math.PI
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

fun main() {
    val center = GeoPoint(37.4220936, -122.083922) // New York City coordinates
    val radius = 0.350 // 350 m radius
    val count = 6 // Number of GeoPoints to generate

    val randomGeoPoints = generateRandomGeoPoints(center, radius, count)

    println("Random GeoPoints within 350 m radius from center:")
    randomGeoPoints.forEach { geoPoint ->
        println("Latitude: ${geoPoint.latitude}, Longitude: ${geoPoint.longitude}")
    }
}
