package uk.ac.tees.mad.w9639109

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class AddCar : AppCompatActivity() {

    private lateinit var carMakesAutoCompleteTextView: AutoCompleteTextView
    private lateinit var carModelsAutoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)


        carMakesAutoCompleteTextView = findViewById(R.id.carBrandAutoCompleteTextView)
        carModelsAutoCompleteTextView = findViewById(R.id.carModelAutoCompleteTextView)


        fetchCarMakes()
    }

    private fun fetchCarMakes() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://car-data.p.rapidapi.com/cars/makes")
            .get()
            .addHeader("X-RapidAPI-Key", "f6d96b246cmsh9f60d5199ed066dp178283jsn2441bcb10c02")
            .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val gson = Gson()
                    val carMakesType = object : TypeToken<List<String>>() {}.type
                    val carMakes: List<String> = gson.fromJson(response.body!!.string(), carMakesType)

                    runOnUiThread {
                        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, carMakes)
                        carMakesAutoCompleteTextView.setAdapter(adapter)
                    }
                }
            }
        })
        carMakesAutoCompleteTextView.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedMake = adapterView.getItemAtPosition(position).toString()
            fetchCarModels(selectedMake)
        }
    }

    private fun fetchCarModels(make: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://car-data.p.rapidapi.com/cars?limit=50&page=0&make=$make")
            .get()
            .addHeader("X-RapidAPI-Key", "f6d96b246cmsh9f60d5199ed066dp178283jsn2441bcb10c02")
            .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val gson = Gson()
                    val carModelsType = object : TypeToken<List<CarModel>>() {}.type
                    val carModels: List<CarModel> = gson.fromJson(response.body!!.string(), carModelsType)

                    val models = carModels.map { it.model }.distinct()

                    runOnUiThread {
                        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, models)
                        carModelsAutoCompleteTextView.setAdapter(adapter)
                    }
                }
            }
        })
    }

    data class CarModel(
        val id: Int,
        val year: Int,
        val make: String,
        val model: String,
        val type: String
    )
}