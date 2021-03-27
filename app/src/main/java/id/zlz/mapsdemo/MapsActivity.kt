package id.zlz.mapsdemo

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    // Uncommecnt jika tidak menggunakan method isMyLocationEnable = true
    private var locationRequest: LocationRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLokasiClient()
        Log.d(TAG, "onCreate: Running" + setupLokasiClient())
    }

    private fun setupLokasiClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d(TAG, "setupLokasiClient: Running")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLocation()
        Log.d(TAG, "onMapReady: Running")
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        Log.d(TAG, "requestLocationPermission: Running")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Log.e(TAG, "LOKASI PERMISSION DITOLAk")
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Running")
    }


    private fun getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: Running")
//        jika permission false
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//        maka set permission
            requestLocationPermission()
        } else {
            // Uncommecnt jika tidak menggunakan method isMyLocationEnable = true
            /* if (locationRequest == null) {
                 locationRequest = LocationRequest.create()
                 locationRequest?.let { locationRequest ->
                     locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                     locationRequest.interval = 5000
                     locationRequest.fastestInterval = 1000

                     val locationCallback = object : LocationCallback() {
                         override fun onLocationResult(locationResult: LocationResult) {
                             getCurrentLocation()
                         }
                     }
                     fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                 }
             }*/
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnCompleteListener() {
//        dapatkan lokasi sesuai lattitude & longitude tampung fusedlocationclient
                val lokasi = it.result
                if (lokasi != null) {
                    val latlong = LatLng(lokasi.latitude, lokasi.longitude)
                    Log.d(TAG, "getCurrentLocation: " + lokasi)
//                    Uncommecnt jika tidak menggunakan method isMyLocationEnable = true
                    /*  mMap.clear()
                      mMap.addMarker(
                              MarkerOptions().position(latlong).title(getString(R.string.app_name))
                      )*/

                    val update = CameraUpdateFactory.newLatLngZoom(latlong, 15.0f)
                    mMap.moveCamera(update)
//                    mMap.animateCamera(CameraUpdateFactory.zoomIn())
                } else {
//        Log E
                    Log.e(TAG, "Lokasi tidak ditemukan ")

                }
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val TAG: String = "MapsActivity"
    }

}