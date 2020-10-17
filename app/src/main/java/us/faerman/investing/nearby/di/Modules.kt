package us.faerman.investing.nearby.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import us.faerman.investing.nearby.managers.location.PositionManager
import us.faerman.investing.nearby.managers.location.geofences.GeoFencesManager
import us.faerman.investing.nearby.managers.locationService
import us.faerman.investing.nearby.managers.provideRetrofitInstance
import us.faerman.investing.nearby.managers.providesGeoFencingClient
import us.faerman.investing.nearby.managers.providesPlacesServiceClient
import us.faerman.investing.nearby.ui.home.HomeViewModel

val appModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
}

val managersModule = module {
    single { locationService(get()) }
    single { PositionManager(get(), get()) }
    single { provideRetrofitInstance() }
    single { providesPlacesServiceClient(get()) }
    single { GeoFencesManager(get(), get()) }
    single { providesGeoFencingClient(get()) }
}

