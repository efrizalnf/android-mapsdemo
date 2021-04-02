package id.zlz.mapsdemo

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    // Uncommecnt jika tidak menggunakan method isMyLocationEnable = true
    private var locationRequest: LocationRequest? = null

    private lateinit var placesClient: PlacesClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        init()
        Log.d(TAG, "onCreate: Running" + setupLokasiClient())
    }


    private fun init() {
        setupLokasiClient()
        setUpPlacesClient()
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

        mMap.setOnPoiClickListener {
//            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
            /** Error*/
            displayPoi(it)
            Log.d(TAG, "onPoiClicklistener: Run ")
        }
    }

    private fun setupLokasiClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d(TAG, "setupLokasiClient: Running")
    }

    private fun setUpPlacesClient() {
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
        Log.d(TAG, "setUpPlacesClient: Running")
    }

    private fun displayPoi(pointOfInterest: PointOfInterest) {
        displayPoiGetPlaceStep(pointOfInterest)

    }

    private fun displayPoiDisplayStep(place: Place, photo:Bitmap?){
        val iconPhoto = if (photo == null){
            BitmapDescriptorFactory.defaultMarker()
        }else {
            BitmapDescriptorFactory.fromBitmap(photo)
        }

        mMap.addMarker(MarkerOptions()
                .position(place.latLng as LatLng)
                .icon(iconPhoto)
                .title(place.name)
                .snippet(place.phoneNumber)
        )
    }


    private fun displayPoiGetPhotoStep(place: Place) {
        /*get Array photo metadata*/
        val photoMetadata = place.photoMetadatas?.get(0)

        if (photoMetadata == null) {
            return
        }

        /*fetch photo , and resize*/
        val photoRequest = FetchPhotoRequest.builder(photoMetadata as PhotoMetadata)
                .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
                .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
                .build()

        /*handle callback*/
        placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener {
                    photoresponses ->
                    val bitmap = photoresponses.bitmap
                    displayPoiDisplayStep(place, bitmap)
                }
                .addOnFailureListener{
                    failureresponses ->
                    if (failureresponses is ApiException){
                        val statusCodes = failureresponses.statusCode
                        Log.e(TAG, "displayPoiGetPhotoStep: " + failureresponses.message +"StatusCode :" + statusCodes )
                    }

                }
    }

    private fun displayPoiGetPlaceStep(pointOfInterest: PointOfInterest) {
        val placeId = pointOfInterest.placeId

        val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHONE_NUMBER,
                Place.Field.PHOTO_METADATAS,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        )

        val requestMapsPlace = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(requestMapsPlace).addOnSuccessListener { response ->
            val place = response.place
            displayPoiGetPhotoStep(place)
//            Toast.makeText(this, "${place.name}," + "${place.phoneNumber}," + "${place.address}," + "${place.latLng}", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Log.e(TAG, "displayPoi: Not found" + exception.message + "Statuscode :" + statusCode)
            }
        }
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

                /**   dapatkan lokasi sesuai lattitude & longitude tampung fusedlocationclient*/
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

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val TAG: String = "MapsActivity"
    }

}