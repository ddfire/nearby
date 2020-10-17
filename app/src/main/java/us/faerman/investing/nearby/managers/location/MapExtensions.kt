package us.faerman.investing.nearby.managers.location

import com.microsoft.maps.MapElementCollection
import com.microsoft.maps.MapIcon

fun MapElementCollection.add(list: ArrayList<MapIcon>) {
    list.forEach{
        this.add(it)
    }
}