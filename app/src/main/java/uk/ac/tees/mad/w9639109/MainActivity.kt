package uk.ac.tees.mad.w9639109

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button:Button = findViewById<Button>(R.id.btnAddCar)

        button.setOnClickListener{
            val i:Intent = Intent(this,AddCar::class.java)
            startActivity(i)
        }
    }


}