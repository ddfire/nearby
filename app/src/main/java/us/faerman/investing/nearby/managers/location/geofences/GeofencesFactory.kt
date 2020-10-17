package us.faerman.investing.nearby.managers.location.geofences

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import uk.me.jstott.jcoord.LatLng
import us.faerman.investing.nearby.model.placesClasses.GeoPoint
import us.faerman.investing.nearby.model.placesClasses.Place

class GeoFencesFactory {
    companion object {
        private val bigPlaces = arrayOf(
            "archipelago",
            "colloquial_area",
            "continent",
            "country",
            "locality",
            "natural_feature",
            "neighborhood",
            "sublocality"
        )

        fun calcDistance(point1: GeoPoint, point2: GeoPoint?, type: List<String>?): Double {
            //If the place has at least one of the type of big places we use the viewport (point2) to generate the geo fence
            //if it is not a big place or point2 is null we use 50 meters. Viewport is designed to set the viewport in map view
            // and show context around the place in question
            //Also viewport is optional, if is bigplace and point2 is null we use 2km as radious
            return if (type!=null && bigPlaces.intersect(type).isNotEmpty()) {
                val p1 = LatLng(point1.lat, point1.lng)
                if (point2 != null) {
                    val p2 = LatLng(point2.lat, point2.lng)
                    p1.distance(p2) * 1000 //convert to meters
                } else {
                    2000.0
                }
            } else {
                50.0
            }
        }

        private fun createGeoFence(id: String, center: GeoPoint, secondPoint: GeoPoint?, type: List<String>): Geofence {
            val  radius = calcDistance(center,secondPoint,type).toFloat()
            return Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(center.lat, center.lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        fun createGeoFenceRequest(places: List<Place>): GeofencingRequest {
            val fences = ArrayList<Geofence>()
            places.forEach{
                val fence = createGeoFence(it.placeId,it.geometry.location,it.geometry.viewport.northeast,it.types)
                fences.add(fence)
            }
            return GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                addGeofences(fences)
            }.build()
        }

    }
}