package us.faerman.investing.nearby

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import us.faerman.investing.nearby.application.NearByApp

class MainActivity : AppCompatActivity() {
    private var dialogShown = false
    private var shouldAsk = true
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            shouldAsk = false
            it.keys.forEach { key ->
                if (!it[key]!!) {
                    //Shown notification explaning and ask for permissions again.
                    Log.d("TAG", "Show notification $key is needed by the app")
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val app = applicationContext as NearByApp
        val service = GoogleApiAvailability.getInstance()
        val permissions =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        if(shouldAsk) {
            requestPermissionLauncher.launch(permissions)
        }
        val playServiceResult =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (playServiceResult != ConnectionResult.SUCCESS) {
            val dialog = service.getErrorDialog(this, playServiceResult, 0)
            if (!dialogShown) {
                dialog.show()
                dialogShown = true
            }
            app.playServiceAvailable = false
        } else {
            app.playServiceAvailable = true
        }
    }
}