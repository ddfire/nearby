package us.faerman.investing.nearby.model.placesClasses

import com.google.gson.annotations.SerializedName

data class GeoPoint(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng:Double
)