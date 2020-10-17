package us.faerman.investing.nearby.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import us.faerman.investing.nearby.ui.home.HomeViewModel

class GeoFenceBroadcastReceiver(private val viewModel: HomeViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geoFencingEvent = GeofencingEvent.fromIntent(intent)

        if (geoFencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geoFencingEvent.errorCode)
            Log.e("GeoFenceBroadcast", errorMessage)
            return
        }

        val triggeringGeoFences = geoFencingEvent.triggeringGeofences
        val ids = ArrayList<String>()
        triggeringGeoFences.forEach {
            ids.add(it.requestId)
        }

        when (geoFencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                viewModel.userEntered(ids)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                viewModel.userLeft(ids)
            }
            else -> {
                Log.e("GeoFenceBroadcast", "Invalid type ${geoFencingEvent.geofenceTransition}")
            }
        }


    }
}