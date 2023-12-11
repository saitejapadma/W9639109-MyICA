package uk.ac.tees.mad.w9639109

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class VehicleService : AppCompatActivity() {

    private lateinit var selectCarDropdown: AutoCompleteTextView
    private lateinit var serviceTypeSpinner: Spinner
    private lateinit var uploadImageButton: Button
    private lateinit var requestServiceButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var carProblemsEditText: EditText
    private  var imageValue:String =""

    private var cars:ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_service)


        selectCarDropdown = findViewById(R.id.selectcardropdown)
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        requestServiceButton = findViewById(R.id.requestServiceButton)
        selectedImageView = findViewById(R.id.selectedImageView)
        carProblemsEditText = findViewById(R.id.carProblemsEditText)



        setupCarDropdown()
        setupServiceTypeSpinner()
        setupImageUpload()
        setupServiceRequest()
    }

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private fun setupImageUpload() {
        uploadImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            selectedImageView.setImageURI(imageUri)
            selectedImageView.visibility = View.VISIBLE

            val outputStream = ByteArrayOutputStream()
            BitmapFactory.decodeStream(imageUri?.let { contentResolver.openInputStream(it) }).compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            imageValue = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        }
    }

    private fun setupCarDropdown() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userCarRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("cars")
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
                    selectCarDropdown.setAdapter(adapter)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    Toast.makeText(applicationContext, "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setupServiceTypeSpinner() {
        val serviceTypes = arrayOf("Regular Checkup", "Intense Checkup")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceTypeSpinner.adapter = adapter
    }

    data class ServiceRequest(
        val car: String,
        val problemDescription: String,
        val serviceType: String,
        val imageUrl: String? = null
    )


    private fun setupServiceRequest() {
        requestServiceButton.setOnClickListener {
            val selectedCar = selectCarDropdown.text.toString()
            val problemDescription = carProblemsEditText.text.toString()
            val serviceType = serviceTypeSpinner.selectedItem.toString()


            if (imageValue != null || imageValue !="") {

                val request = ServiceRequest(selectedCar, problemDescription, serviceType, imageValue)


                sendServiceRequestToDatabase(request)
            } else {

                val request = ServiceRequest(selectedCar, problemDescription, serviceType, null)
                sendServiceRequestToDatabase(request)
            }
        }
    }

    private fun sendServiceRequestToDatabase(request: ServiceRequest) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("serviceRequests")
        databaseReference.push().setValue(request)
            .addOnSuccessListener {
                Toast.makeText(this, "Service request submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit service request: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}