package us.faerman.investing.nearby.managers.network

import retrofit2.http.GET
import retrofit2.http.Query
import us.faerman.investing.nearby.model.PlacesSearchResponse


interface PlacesService {
    @GET("nearbysearch/json?key=AIzaSyDjtsuytgIAYLmczXOXrcv1Xt1ZbZXHowY")
    suspend fun searchNearBy(
        @Query("location") latLong: String,
        @Query("radius") radius: String,
        @Query("type") type: String,
        @Query("keyboard") keyboard: String
    ): PlacesSearchResponse

    @GET("nearbysearch/json?key=AIzaSyDjtsuytgIAYLmczXOXrcv1Xt1ZbZXHowY")
    suspend fun searchNearBy(
        @Query("location") latLong: String,
        @Query("radius") radius: String
    ): PlacesSearchResponse

    @GET("photo?key=AIzaSyDjtsuytgIAYLmczXOXrcv1Xt1ZbZXHowY&maxwidth=400")
    suspend fun getPhotoByIdea(@Query("photoreference") reference: String)
}