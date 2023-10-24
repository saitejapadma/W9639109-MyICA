package com.example.switchcaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.switchcaseapp.ui.theme.SwitchCaseAppTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwitchCaseAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SwitchCaseText("Android")
                }
            }
        }
    }
}

fun switchCase(str: String): String {
    return str.map {
            it -> when(it) {
        in 'a' .. 'z' -> it - 32
        in 'A' .. 'Z' -> it + 32
        else -> it
    }
    }.joinToString("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchCaseText(name: String, modifier: Modifier = Modifier) {
    var userMessage by remember {
        mutableStateOf("Hello")
    }

    Column {
        TextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            label = { "Enter Something " }
        )

        Text(
            text = "Hello ${switchCase(userMessage)}!",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SwitchCaseAppTheme {
        SwitchCaseText("Android")
    }
}