package us.faerman.investing.nearby.model.placesClasses

import com.google.gson.annotations.SerializedName

data class OpeningHours (@SerializedName("open_now") val openNow: Boolean? = null)
