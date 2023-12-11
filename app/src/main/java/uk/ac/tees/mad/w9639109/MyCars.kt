package uk.ac.tees.mad.w9639109

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyCars : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCarsScreen()
        }
    }

    @Composable
    fun MyCarsScreen() {
        val cars = remember { mutableStateListOf<Car>() } // Assuming Car is your data class

        // Fetch cars from Firebase
        LaunchedEffect(key1 = Unit) {
            val databaseReference =
                FirebaseAuth.getInstance().currentUser?.let {
                    FirebaseDatabase.getInstance().getReference("users").child(
                        it.uid).child("cars")
                }
            if (databaseReference != null) {
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        cars.clear()
                        snapshot.children.forEach { dataSnapshot ->
                            val car = dataSnapshot.getValue(Car::class.java)
                            car?.let { cars.add(it) }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }


        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "My Cars",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(1f, true))


            cars.forEach { car ->
                CarItem(car, onDelete = { deleteCar(car) })
            }

            Spacer(modifier = Modifier.weight(1f, true))

            // Go Back Button
            Button(
                onClick = { finish() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Go Back")
            }
        }
    }

    @Composable
    fun CarItem(car: Car, onDelete: () -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = car.name, modifier = Modifier.weight(1f))
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }

    fun deleteCar(carToDelete: Car) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid).child("cars")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (carSnapshot in snapshot.children) {
                        val car = carSnapshot.getValue(Car::class.java)
                        if (car != null && car.name == carToDelete.name) {
                            carSnapshot.ref.removeValue() // Delete the car
                            break // Exit the loop once the car is found and deleted
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                }
            })
        }
    }



}