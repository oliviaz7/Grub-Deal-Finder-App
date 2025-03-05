package com.example.grub.ui.map

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import kotlin.math.pow
import kotlin.math.sqrt

// based off of: https://stackoverflow.com/questions/29222864/get-radius-of-visible-map-in-android

fun getVisibleRadius(projection: Projection?): Double {
    if (projection == null) {
        Log.d("radius-projection", "Projection is NULL, radius is set to 0!")
        return 0.0
    }

    val visibleRegion = projection.visibleRegion
    val farRight: LatLng = visibleRegion.farRight
    val farLeft: LatLng = visibleRegion.farLeft
    val nearRight: LatLng = visibleRegion.nearRight
    val nearLeft: LatLng = visibleRegion.nearLeft

    val distanceWidth = FloatArray(1)
    val distanceHeight = FloatArray(1)

    Location.distanceBetween(
        (farLeft.latitude + nearLeft.latitude) / 2,
        farLeft.longitude,
        (farRight.latitude + nearRight.latitude) / 2,
        farRight.longitude,
        distanceWidth
    )

    Location.distanceBetween(
        farRight.latitude,
        (farRight.longitude + farLeft.longitude) / 2,
        nearRight.latitude,
        (nearRight.longitude + nearLeft.longitude) / 2,
        distanceHeight
    )

    val diagonal = sqrt(distanceWidth[0].toDouble().pow(2.0) + distanceHeight[0].toDouble().pow(2.0))

    Log.d("radius-projection", "diameter of circle: ${(diagonal / 2).toFloat()}")
    return (diagonal / 2).toDouble()
}
