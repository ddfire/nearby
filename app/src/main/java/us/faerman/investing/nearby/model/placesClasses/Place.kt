package us.faerman.investing.nearby.model.placesClasses


import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("business_status")
    val businessStatus: String? = null,
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("opening_hours")
    val openingHours: OpeningHours? = null,
    @SerializedName("photos")
    val photos: List<Photo>? = null,
    @SerializedName("place_id")
    val placeId: String,
/*    @SerializedName("plus_code")
    val plusCode: PlusCode? = null,*/
    @SerializedName("price_level")
    val priceLevel: Int? = null,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("reference")
    val reference: String? = null,
    @SerializedName("scope")
    val scope: String? = null,
    @SerializedName("types")
    val types: List<String>,
    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int? = null,
    @SerializedName("vicinity")
    val vicinity: String? = null,
    @SerializedName("permanently_closed")
    val permanentlyClosed: Boolean? = null
)