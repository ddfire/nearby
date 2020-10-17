package us.faerman.investing.nearby.model.placesClasses

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>? = null,
    @SerializedName("photo_reference")
    val photoReference: String? = null,
    @SerializedName("width")
    val width: Int? = null
)