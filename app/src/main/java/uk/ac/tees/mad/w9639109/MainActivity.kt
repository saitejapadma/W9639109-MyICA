package uk.ac.tees.mad.w9639109

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat

import androidx.biometric.BiometricPrompt
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button:Button = findViewById<Button>(R.id.btnAddCar)

        button.setOnClickListener{
            val i:Intent = Intent(this,AddCar::class.java)
            startActivity(i)
        }
        findViewById<RelativeLayout>(R.id.breaksdown).setOnClickListener{
            startActivity(Intent(applicationContext,BreakDownAssistence::class.java))
        }
        findViewById<RelativeLayout>(R.id.near).setOnClickListener{
            startActivity(Intent(applicationContext,ServiceStation::class.java))
        }
        findViewById<RelativeLayout>(R.id.serviceVechile).setOnClickListener{
            startActivity(Intent(applicationContext,VehicleService::class.java))
        }
        findViewById<RelativeLayout>(R.id.mycars).setOnClickListener{
            startActivity(Intent(applicationContext,MyCars::class.java))
        }
        findViewById<RelativeLayout>(R.id.ask).setOnClickListener{
            startActivity(Intent(applicationContext,FAQActivity::class.java))
        }
        initiateBIOVerify()
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(applicationContext,SignIn::class.java))
        }

    }

    private fun initiateBIOVerify() {
            val execu = ContextCompat.getMainExecutor(this)
            val prompt = BiometricPrompt(this, execu, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
            })

            val info = BiometricPrompt.PromptInfo.Builder().setTitle("Authentication")
                .setSubtitle("Log IN")
                .setNegativeButtonText("Unable to loign").build()

            prompt.authenticate(info)

    }

}