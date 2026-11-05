package com.example.test2

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class PlannedDayResult(
    val orderedPlaces: List<PlaceItem>,
    val routePolylinePoints: List<LatLng>
)

object RoutePlanner {

    /**
     * Reorders the input places into an optimal visiting order (TSP Nearest Neighbor)
     * and inserts a Food Stop and a Rest Stop at appropriate points along the way.
     */
    fun planDay(places: List<PlaceItem>): PlannedDayResult {
        if (places.isEmpty()) {
            return PlannedDayResult(emptyList(), emptyList())
        }

        // 1. Filter places to visit (or include all active places)
        val toVisit = places.toMutableList()

        // 2. Compute TSP visiting order using Nearest-Neighbor heuristic
        val orderedList = mutableListOf<PlaceItem>()
        var current = toVisit.removeAt(0)
        orderedList.add(current)

        while (toVisit.isNotEmpty()) {
            val nearestIndex = toVisit.indices.minByOrNull { i ->
                distanceKm(current.latitude, current.longitude, toVisit[i].latitude, toVisit[i].longitude)
            } ?: 0
            current = toVisit.removeAt(nearestIndex)
            orderedList.add(current)
        }

        // 3. Insert Food Stop & Rest Stop at suitable points
        val resultList = mutableListOf<PlaceItem>()
        val totalPlaces = orderedList.size

        if (totalPlaces == 1) {
            resultList.add(orderedList[0])

            // Food stop near place 1
            val foodLat = orderedList[0].latitude + 0.003
            val foodLng = orderedList[0].longitude + 0.003
            resultList.add(
                PlaceItem(
                    name = "Tasty Bites Bistro (Food Stop)",
                    latitude = foodLat,
                    longitude = foodLng,
                    isFoodStop = true
                )
            )

            // Rest stop near place 1
            val restLat = orderedList[0].latitude - 0.003
            val restLng = orderedList[0].longitude + 0.003
            resultList.add(
                PlaceItem(
                    name = "Central Oasis Lounge (Rest Stop)",
                    latitude = restLat,
                    longitude = restLng,
                    isRestStop = true
                )
            )
        } else {
            // Food stop around halfway (50%)
            val foodIndex = (totalPlaces / 2).coerceAtLeast(1)

            for (i in 0 until totalPlaces) {
                resultList.add(orderedList[i])

                if (i == foodIndex - 1) {
                    val prev = orderedList[i]
                    val next = orderedList.getOrElse(i + 1) { prev }
                    val midLat = (prev.latitude + next.latitude) / 2.0 + 0.0015
                    val midLng = (prev.longitude + next.longitude) / 2.0 + 0.0015
                    resultList.add(
                        PlaceItem(
                            name = "Sunburst Cafe & Grill (Food Stop)",
                            latitude = midLat,
                            longitude = midLng,
                            isFoodStop = true
                        )
                    )
                }

                // Rest stop near 3/4 or near the end of the trip
                val restIndex = ((totalPlaces * 3) / 4).coerceAtLeast(1)
                if (i == restIndex && i != foodIndex - 1) {
                    val prev = orderedList[i]
                    val next = orderedList.getOrElse(i + 1) { prev }
                    val midLat = (prev.latitude + next.latitude) / 2.0 - 0.0015
                    val midLng = (prev.longitude + next.longitude) / 2.0 - 0.0015
                    resultList.add(
                        PlaceItem(
                            name = "Grand Park & Rest Lounge (Rest Stop)",
                            latitude = midLat,
                            longitude = midLng,
                            isRestStop = true
                        )
                    )
                }
            }

            // Ensure rest stop is included if not added yet
            if (resultList.none { it.isRestStop }) {
                val last = resultList.last()
                resultList.add(
                    PlaceItem(
                        name = "Haven Relax Spot (Rest Stop)",
                        latitude = last.latitude - 0.002,
                        longitude = last.longitude + 0.002,
                        isRestStop = true
                    )
                )
            }
        }

        // 4. Interpolate smooth polyline points for route drawing on Google Maps
        val routePoints = mutableListOf<LatLng>()
        for (i in 0 until resultList.size - 1) {
            val p1 = resultList[i]
            val p2 = resultList[i + 1]
            val interpolated = interpolateWaypoints(
                LatLng(p1.latitude, p1.longitude),
                LatLng(p2.latitude, p2.longitude),
                steps = 10
            )
            routePoints.addAll(interpolated)
        }
        if (resultList.isNotEmpty()) {
            val last = resultList.last()
            routePoints.add(LatLng(last.latitude, last.longitude))
        }

        return PlannedDayResult(resultList, routePoints)
    }

    private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun interpolateWaypoints(start: LatLng, end: LatLng, steps: Int): List<LatLng> {
        val list = mutableListOf<LatLng>()
        for (i in 0..steps) {
            val fraction = i.toDouble() / steps
            val lat = start.latitude + (end.latitude - start.latitude) * fraction
            val lng = start.longitude + (end.longitude - start.longitude) * fraction
            list.add(LatLng(lat, lng))
        }
        return list
    }
}
