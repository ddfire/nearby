package us.faerman.investing.nearby.managers.location.geofences

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.GeofencingClient
import us.faerman.investing.nearby.application.NearByApp
import us.faerman.investing.nearby.model.placesClasses.Place

class GeoFencesManager(private val context: Context, private val client: GeofencingClient) {
    private val fences = HashMap<String,Place>(10)


    fun getGeoFenceDataById(id:String):Place?{
        return fences[id]
    }

    fun addGeoFence(place: Place){
        addGeoFence(arrayListOf(place))
    }

    fun addGeoFence(places: List<Place>){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //I am asking permissions when the actity starts, no need to ask again if the user said no
            return
        }

        if(!(context as NearByApp).playServiceAvailable){
            return
        }

        val intent = Intent(GEOFENCE_INTENT_FILTER)
        val pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        places.forEach{
            fences[it.placeId] = it
        }
        client.addGeofences(GeoFencesFactory.createGeoFenceRequest(places),pi)?.run {
            addOnSuccessListener {

            }
            addOnFailureListener {
                Log.e("client","error adding geofences, $it")
            }
        }
    }

    fun removeGeoFence(place:Place){
        removeGeoFence(arrayListOf(place))
        fences.remove(place.placeId)
    }

    fun removeGeoFence(places:List<Place>){
        val ids = ArrayList<String>()
        places.forEach{
            ids.add(it.placeId)
            fences.remove(it.placeId)
        }
        client.removeGeofences(ids)
    }

    fun clearAllFences(){
        removeGeoFence(fences.values.toList())
    }

    companion object{
        const val GEOFENCE_INTENT_FILTER = "us.faerman.investing.nearby.geofence.event"
    }

}