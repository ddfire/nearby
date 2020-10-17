package us.faerman.investing.nearby.utils

import com.microsoft.maps.MapDragEventArgs
import com.microsoft.maps.MapView
import com.microsoft.maps.OnMapDragListener
import us.faerman.investing.nearby.model.placesClasses.GeoPoint

fun MapView.addOnfilteredDragEvent(
    every: Long,
    dragEventListener: DragEventHandler.DragEventListener<GeoPoint>
) {
    this.addOnMapDragListener(object : OnMapDragListener {
        val listener = DragEventHandler(every,true, dragEventListener)
        override fun onMapDrag(p0: MapDragEventArgs?): Boolean {
            val pos = this@addOnfilteredDragEvent.center.position
            listener.onDragEvent(GeoPoint(pos.latitude, pos.longitude))
            return true
        }
    })
}