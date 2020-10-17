package us.faerman.investing.nearby.managers.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import us.faerman.investing.nearby.application.NearByApp

class PositionManager(private val context: Context, private val locationManager: LocationManager) :
    LocationListener {
    @ExperimentalCoroutinesApi
    private val _events = MutableSharedFlow<Location>() // private mutable shared flow
    @ExperimentalCoroutinesApi
    val events = _events.asSharedFlow() // publicly exposed as read-only shared flow

    private var consumers =0

    private var running: Boolean = false
    val isRunning get() = running

    fun starProvidingPosition(listener: PositionManagerListener): Boolean {
        if(!(context as NearByApp).playServiceAvailable){
            listener.onPlayServiceDisable()
            return false
        }
        if (running) {
            consumers++
            return true
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            listener.onPermissionError()
            return false
        } else {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isCostAllowed = false
            locationManager.requestLocationUpdates(1000, 3f, criteria, this, null)
            running = true
        }
        listener.onReady()
        consumers++
        return true
    }

    fun stopProvidingPosition() {
        consumers--
        if(consumers != 0){
            return
        }
        locationManager.removeUpdates(this)
        running = false
    }

    @ExperimentalCoroutinesApi
    override fun onLocationChanged(location: Location) {
        Log.d("TAG", "New position: $location")
        GlobalScope.launch {
            _events.emit(location)
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("TAG", "onProviderEnabled: $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("TAG", "onProviderDisabled: $provider")
    }

}