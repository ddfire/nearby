package us.faerman.investing.nearby.model.placesClasses


import com.google.gson.annotations.SerializedName

data class Geometry (
    @SerializedName("location")
    val location: GeoPoint,
    @SerializedName("viewport")
    val viewport: Viewport
)