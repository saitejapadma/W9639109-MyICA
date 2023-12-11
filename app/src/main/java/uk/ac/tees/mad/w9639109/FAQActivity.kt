package uk.ac.tees.mad.w9639109

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class FAQActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FAQScreen()
        }
    }

    @Composable
    fun FAQScreen() {
        // Sample data
        val faqList = remember {
            mutableStateListOf(
                FAQItem("What regular maintenance does my car need?", "Regular maintenance includes oil changes, tire rotations, brake checks, and engine inspections."),
                FAQItem("How often should I change my oil?", "Most vehicles require an oil change every 5,000 to 7,500 miles. Refer to your car's manual for specific guidelines."),
                FAQItem("What should I do if my car overheats?", "If your car overheats, pull over safely, turn off the engine, and wait for it to cool down before checking the coolant level."),
                FAQItem("How do I know if my brakes need servicing?", "Signs your brakes need servicing include squeaking noises, a vibrating steering wheel, or a longer stopping distance."),

            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("FAQs", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))


            faqList.forEach { faqItem ->
                FAQItemView(faqItem)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    @Composable
    fun FAQItemView(faqItem: FAQItem) {
        Card( modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(faqItem.question, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(faqItem.answer)
            }
        }
    }
}

data class FAQItem(val question: String, val answer: String)

