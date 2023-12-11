package uk.ac.tees.mad.w9639109

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BreakDownAssistence : AppCompatActivity() , OnMapReadyCallback {


    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var carNumberAutoComplete: AutoCompleteTextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentLatLng: LatLng
    private var cars:ArrayList<String> = ArrayList()

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_down_assistence)

        findViewById<Button>(R.id.goBack).setOnClickListener{
            raiseRequest()
        }

        carNumberAutoComplete = findViewById(R.id.carnames)
        databaseReference = FirebaseDatabase.getInstance().getReference("cars")




        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchUserCars()

    }


    private fun fetchUserCars() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userCarRef = database.getReference("users").child(uid).child("cars")
            userCarRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (snapshot in dataSnapshot.children) {
                        val car = snapshot.getValue(Car::class.java)
                        car?.let {
                            cars.add(it.name)
                        }
                    }
                    val adapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_dropdown_item_1line,
                        cars
                    )
                    carNumberAutoComplete.setAdapter(adapter)

                }

                override fun onCancelled(databaseError: DatabaseError) {

                    Toast.makeText(applicationContext, "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
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
                     currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        } else {

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun raiseRequest() {
        val location = "" + currentLatLng.latitude + "," + currentLatLng.longitude
        val carNumber = carNumberAutoComplete.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val request = BreakdownRequest(location, carNumber, date, userId)


        val databaseReference = FirebaseDatabase.getInstance().getReference("breakdownRequests")
        databaseReference.push().setValue(request)
            .addOnSuccessListener {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Request raised, Towering Vehicle Reaches soon", Snackbar.LENGTH_LONG)
                snackbar.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 3000)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to raise request: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }



}

data class BreakdownRequest(
    val location: String,
    val carNumber: String,
    val date: String,
    val userId: String?
)
