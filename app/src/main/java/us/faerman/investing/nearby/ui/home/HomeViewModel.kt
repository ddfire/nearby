package us.faerman.investing.nearby.ui.home

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.maps.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import us.faerman.investing.nearby.managers.location.PositionManager
import us.faerman.investing.nearby.managers.location.PositionManagerListener
import us.faerman.investing.nearby.managers.location.geofences.GeoFencesFactory
import us.faerman.investing.nearby.managers.location.geofences.GeoFencesManager
import us.faerman.investing.nearby.managers.network.PlacesService
import us.faerman.investing.nearby.model.placesClasses.GeoPoint

class HomeViewModel(
    private val positionManager: PositionManager,
    private val placesService: PlacesService,
    private val geoFencesManager: GeoFencesManager
) : ViewModel(),
    PositionManagerListener {

    private val fences = HashMap<String, MapPolygon>(10)
    val locationUpdated = MutableLiveData<Location>()
    val pinsUpdated = MutableLiveData<ArrayList<MapIcon>>()
    val locationProviderOff = MutableLiveData<String>()
    val fencesLive = MutableLiveData<ArrayList<MapPolygon>>()
    val fencesLiveLeft = MutableLiveData<List<MapPolygon>>()

    private var isMapCentered: Boolean = true
    private var stopUpdating = false
    private var lastLocation: Location? = null
    private var lastDraggedPosition: GeoPoint? = null
    private var radius: Double = 0.0

    fun onCenterMapClick() {
        isMapCentered = true
        lastLocation?.let {
            updateMapPosition(it)
            getNewPOI(GeoPoint(it.latitude, it.longitude))
        }
    }

    private fun updateMapPosition(location: Location) {
        locationUpdated.value = location
    }

    fun mapDraggedPosition(position: GeoPoint) {
        isMapCentered = false
        lastDraggedPosition = position
        getNewPOI(position)
    }

    //Empiric equation to find POI's only in the area displayed by the map
    fun setZoomLevel(zoomLevel: Double) {
        radius = (1 / zoomLevel) * 1000
    }

    fun stopPositionManager() {
        positionManager.stopProvidingPosition()
        stopUpdating = true
    }

    fun userEntered(ids: List<String>) {
        val polys = ArrayList<MapPolygon>(10)
        ids.forEach { id ->
            val place = geoFencesManager.getGeoFenceDataById(id)
            place?.let {
                val point = place.geometry.location
                val radius = GeoFencesFactory.calcDistance(
                    point,
                    place.geometry.viewport.northeast,
                    place.types
                )
                val circle = Geocircle(Geoposition(point.lat, point.lng), radius)
                val poly = MapPolygon()
                poly.apply {
                    shapes = arrayListOf(circle)
                    fillColor = 0x55FF0000
                }
                fences[it.placeId] = poly
                polys.add(poly)
            }
        }
        fencesLive.value = polys
    }

    fun userLeft(ids: ArrayList<String>) {
        val polys = ArrayList<MapPolygon>(10)
        ids.forEach {
            val poly = fences.remove(it)
            if (poly != null) {
                polys.add(poly)
            }
        }
        fencesLiveLeft.value = polys
    }

    @ExperimentalCoroutinesApi
    fun getPositions() {
        stopUpdating = false
        if (!positionManager.isRunning) {
            positionManager.starProvidingPosition(this)
        }
        viewModelScope.launch {
            positionManager.events.conflate().collect {
                lastLocation = it
                if (isMapCentered) {
                    getNewPOI(GeoPoint(it.latitude, it.longitude))
                    locationUpdated.postValue(it)
                }
                if (stopUpdating) {
                    cancel()
                }
            }
        }
    }

    private fun getNewPOI(position: GeoPoint) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = placesService.searchNearBy(
                "${position.lat},${position.lng}",
                radius.toString()
            )
            Log.e("RESULT", "$result")
            result.places?.let {
                val icons = ArrayList<MapIcon>()
                if (it.isNotEmpty()) {
                    geoFencesManager.clearAllFences()
                    it.forEachIndexed loop@{ index, item ->
                        if (index > 4) {
                            return@loop
                        }
                        val icon = MapIcon()
                        icon.apply {
                            val pos = item.geometry.location
                            location = Geopoint(pos.lat, pos.lng)
                            title = item.name
                        }
                        icons.add(icon)
                        fencesLiveLeft.postValue(fences.values.toList())
                        geoFencesManager.addGeoFence(item)
                    }

                    pinsUpdated.postValue(icons)
                }
            }
        }
    }

    override fun onGpsProviderDisable() {

    }

    override fun onNoProviderAvailable() {

    }

    override fun onInitializeProviderError() {
    }

    override fun onPermissionError() {

    }

    override fun onReady() {

    }

    override fun onPlayServiceDisable() {
        Log.e("ERROR", "onPlayServiceDisable")
    }

}