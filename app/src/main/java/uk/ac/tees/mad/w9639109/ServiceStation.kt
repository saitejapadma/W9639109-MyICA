package uk.ac.tees.mad.w9639109

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ServiceStation : AppCompatActivity()  , OnMapReadyCallback {

    private lateinit var database: FirebaseDatabase
    private lateinit var autheByFirebase: FirebaseAuth
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var carNumberAutoComplete: AutoCompleteTextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentLatLng: LatLng

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_station)

        databaseReference = FirebaseDatabase.getInstance().getReference("cars")

        autheByFirebase = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.goBack).setOnClickListener{
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        setupLocationUpdates()

        startLocationUpdates()
    }

    private fun setupLocationUpdates() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data

                    val markerIcon = resizeMapIcons(applicationContext,R.drawable.one,25,25)

                    currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                    mMap.addMarker(MarkerOptions().position(LatLng(51.54759821971691, -0.025005165901256725)).title("Current Location").icon(markerIcon))
                    mMap.addMarker(MarkerOptions().position(LatLng(51.548992621208576, -0.028545681590412895)).title("Current Location").icon(markerIcon))
                    mMap.addMarker(MarkerOptions().position(LatLng(51.558503649336586, -0.03981740189594692)).title("Current Location").icon(markerIcon))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
                }
            }
        }
    }
    fun resizeMapIcons(context: Context, iconName: Int, width: Int, height: Int): BitmapDescriptor {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, iconName)
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}