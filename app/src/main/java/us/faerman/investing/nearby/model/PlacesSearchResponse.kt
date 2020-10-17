package us.faerman.investing.nearby.model


import com.google.gson.annotations.SerializedName
import us.faerman.investing.nearby.model.placesClasses.Place

//\@[a-z]*:JsonProperty\(\"[a-z_]*\"\)
data class PlacesSearchResponse (
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>? = null,

    @SerializedName("next_page_token")
    val nextPageToken: String? = null,

    @SerializedName("results")
    val places: List<Place>? = null,

    @SerializedName("status")
    val status: String? = null
)
