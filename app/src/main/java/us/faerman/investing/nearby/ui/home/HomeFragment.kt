package us.faerman.investing.nearby.ui.home

import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.microsoft.maps.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import us.faerman.investing.nearby.BuildConfig
import us.faerman.investing.nearby.R
import us.faerman.investing.nearby.broadcastReceiver.GeoFenceBroadcastReceiver
import us.faerman.investing.nearby.databinding.FragmentHomeBinding
import us.faerman.investing.nearby.managers.location.add
import us.faerman.investing.nearby.managers.location.geofences.GeoFencesManager
import us.faerman.investing.nearby.model.placesClasses.GeoPoint
import us.faerman.investing.nearby.utils.DragEventHandler
import us.faerman.investing.nearby.utils.addOnfilteredDragEvent

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    //prevents memory leaks
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var br:GeoFenceBroadcastReceiver
    private lateinit var mapView: MapView
    private val pinLayer = MapElementLayer()
    private val userLayer = MapElementLayer()
    private lateinit var userImage: MapImage
    val userPin = MapIcon()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this

        mapView = MapView(
            requireContext(),
            MapRenderMode.RASTER
        ) // or use MapRenderMode.RASTER for 2D map
        mapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY)
        mapView.layers.add(pinLayer)
        mapView.layers.add(userLayer)

        binding.mapView.addView(mapView)
        val icon = BitmapFactory.decodeResource(
            requireContext().resources,
            R.drawable.i_logo
        )
        userImage = MapImage(icon)

        val center = Geopoint(mapView.center.position)
        userPin.apply {
            location = center
            image = userImage
        }
        userLayer.elements.add(userPin)

        //Extension function added because Bing maps has no control over how often send an update, neither when the drag start or ends.
        //Also Google Place API cost money we want a fluid experience but not waste money.
        mapView.addOnfilteredDragEvent(300, object : DragEventHandler.DragEventListener<GeoPoint> {
            override fun onDragStarted(value: GeoPoint) {
                Log.e("DragEventHandler", "onDragStarted")
                homeViewModel.mapDraggedPosition(value)
            }

            override fun onDragStopped(value: GeoPoint) {
                Log.e("DragEventHandler", "onDragStopped")
                homeViewModel.mapDraggedPosition(value)
            }

            override fun onIntermediateValue(value: GeoPoint) {
                Log.e("DragEventHandler", "onIntermediateValue $value")
                homeViewModel.mapDraggedPosition(value)
            }

            override fun onUnfiltered(value: GeoPoint) {
                val mapCenter = Geopoint(mapView.center.position)
                userPin.apply {
                    location = mapCenter
                }
            }

        })

        mapView.addOnMapCameraChangedListener {
            val mapCenter = Geopoint(mapView.center.position)

//            userLayer.elements.clear()

            Log.e("ZOOM","ZOOM LEVEL ${mapView.zoomLevel}")
            userPin.apply {
                location = mapCenter
            }
            homeViewModel.setZoomLevel(mapView.zoomLevel)

     //       userLayer.elements.add(userPin)
            true
        }

        homeViewModel.locationProviderOff.observe(viewLifecycleOwner) {
            //Show notification on dismiss
            enableLocationSettings()
        }

        homeViewModel.locationUpdated.observe(viewLifecycleOwner) {
            centerMap(it)
        }
        homeViewModel.pinsUpdated.observe(viewLifecycleOwner) {
            pinLayer.elements.clear()
            pinLayer.elements.add(it)
        }

        homeViewModel.fencesLive.observe(viewLifecycleOwner){
           it.forEach { poly ->
               pinLayer.elements.add(poly)
           }
        }
        homeViewModel.fencesLiveLeft.observe(viewLifecycleOwner){
            it.forEach { poly ->
                pinLayer.elements.remove(poly)
            }
        }


        return binding.root
    }

    private fun centerMap(location: Location) {
        val scene = MapScene.createFromLocation(Geopoint(location.latitude, location.longitude))
        mapView.setScene(scene, MapAnimationKind.DEFAULT)
    }

    private fun enableLocationSettings() {
        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

    @ExperimentalCoroutinesApi
    override fun onResume() {
        super.onResume()
        br = GeoFenceBroadcastReceiver(homeViewModel)
        requireContext().registerReceiver(br, IntentFilter(GeoFencesManager.GEOFENCE_INTENT_FILTER))
        homeViewModel.getPositions()
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(br)
        homeViewModel.stopPositionManager()
    }

    //prevents memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}