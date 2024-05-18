package com.mygdx.game.presentation.map.utility

import android.graphics.Rect
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

 fun MapView.findMarker(x: Float, y: Float): Marker? {
    val markerSize = 100 // Assuming a default marker size in pixels
    val enlargedSize = markerSize * 1 // Doubling the marker size for enlargement

    for (overlay in overlays) {
        if (overlay is Marker && overlay.position != null) {
            val markerPoint = projection.toPixels(overlay.position, null)
            val markerBounds = Rect(
                markerPoint.x - enlargedSize / 2, // Adjusted to use enlarged size
                markerPoint.y - enlargedSize / 2, // Adjusted to use enlarged size
                markerPoint.x + enlargedSize / 2, // Adjusted to use enlarged size
                markerPoint.y + enlargedSize / 2 // Adjusted to use enlarged size
            )
            if (markerBounds.contains(x.toInt(), y.toInt())) {
                return overlay // Found the marker at the touch coordinates
            }
        }
    }
    return null // No marker found at the touch coordinates
}


enum class MapItemType {
    ME,
    OBJECT,
    OTHER
}