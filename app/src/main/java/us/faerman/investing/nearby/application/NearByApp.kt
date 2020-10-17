package us.faerman.investing.nearby.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import us.faerman.investing.nearby.di.appModule
import us.faerman.investing.nearby.di.managersModule

class NearByApp : Application() {
    var playServiceAvailable = true

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NearByApp)
            modules(listOf(appModule,managersModule))
        }
    }


}