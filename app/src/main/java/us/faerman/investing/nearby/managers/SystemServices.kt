package us.faerman.investing.nearby.managers

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.faerman.investing.nearby.managers.network.PlacesService
import java.util.concurrent.TimeUnit

fun locationService(context: Context): LocationManager {
    return context.getSystemService(LOCATION_SERVICE) as LocationManager
}

fun providesGeoFencingClient(context: Context): GeofencingClient {
    return LocationServices.getGeofencingClient(context)
}

fun provideRetrofitInstance(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/place/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun providesPlacesServiceClient(retrofit: Retrofit): PlacesService {
    return retrofit.create(PlacesService::class.java)
}

//Only used for debug
private fun provideOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
    return OkHttpClient().newBuilder()
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .addInterceptor(logging)
        .build()
}