package us.faerman.investing.nearby.model.placesClasses

import com.google.gson.annotations.SerializedName

data class Viewport(
    @SerializedName("northeast")
    var northeast: GeoPoint? = null,
    @SerializedName("southwest")
    var southwest: GeoPoint? = null
)