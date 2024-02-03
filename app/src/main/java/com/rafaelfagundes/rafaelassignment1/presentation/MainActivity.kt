package com.rafaelfagundes.rafaelassignment1.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.rafaelfagundes.rafaelassignment1.presentation.theme.RafaelAssignment1Theme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    RafaelAssignment1Theme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            StopwatchScreen()
        }
    }
}

@Composable
fun StopwatchScreen() {

    // Stopwatch button colors
    // Start button color
    val startColor = "#32CD32"
    val startColour = ButtonDefaults.buttonColors(backgroundColor = Color(android.graphics.Color.parseColor(startColor)))

    // Stop button color
    val stopColor = "#FF6347"
    val stopColour = ButtonDefaults.buttonColors(backgroundColor = Color(android.graphics.Color.parseColor(stopColor)))

    // Reset button color
    val resetColor = "#1E90FF"
    val resetColour = ButtonDefaults.buttonColors(backgroundColor = Color(android.graphics.Color.parseColor(resetColor)))

    // Coroutine scope
    val scope = rememberCoroutineScope()

    // Button shapes
    val roundedRectangleShape = RoundedCornerShape(8.dp)

    // Button sizes
    val buttonSize = Modifier.size(width=140.dp, height=40.dp)

    // Stopwatch state
    var startTime by remember { mutableStateOf(0L) } // Start time in nanoseconds
    var elapsedTime by remember { mutableStateOf(0L) } // Elapsed time in milliseconds
    var timerJob: Job? by remember { mutableStateOf(null) } // Timer job

    Column(
        modifier = Modifier.fillMaxSize(), // Fill the entire screen
        horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp)) // Add some space at the top

        // Display the elapsed time in hours:minutes:seconds.milliseconds
        Text(text = String.format("%02d:%02d:%02d.%03d", elapsedTime / 3600000, (elapsedTime % 3600000) / 60000, (elapsedTime % 60000) / 1000, (elapsedTime % 1000)), style = MaterialTheme.typography.title2)
        Spacer(modifier = Modifier.height(6.dp)) // Add some space between the time and the buttons
        Button(
            onClick = {
                // Get the current time in nanoseconds
                startTime = System.nanoTime()

                timerJob = scope.launch {
                    // Update the elapsed time every millisecond
                    while (true) {
                        elapsedTime = (System.nanoTime() - startTime) / 1_000_000 // Convert nanoseconds to milliseconds
                        delay(1L) // Wait for 1 millisecond
                    }
                }
            },

            // Button Configuration
            shape = roundedRectangleShape,
            modifier = buttonSize,
            colors = startColour,

            // Enable the button if the timer is not running
            enabled = timerJob == null
        ) {
            Text("Start")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            // Stop the timer at the current elapsed time
            onClick = {
                timerJob?.cancel() // Cancel the timer job
                timerJob = null // Set the timer job to null
            },

            // Button Configuration
            shape = roundedRectangleShape,
            modifier = buttonSize,
            colors = stopColour,

            // Enable the button if the timer is running
            enabled = timerJob != null
        ) {
            Text("Stop")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            // Reset the elapsed time to 0
            onClick = {
                elapsedTime = 0L // Set the elapsed time to 0
                timerJob?.cancel() // Cancel the timer job
                timerJob = null // Set the timer job to null
            },

            // Button Configuration
            shape = roundedRectangleShape,
            modifier = buttonSize,
            colors = resetColour,

            // Enable the button if the timer is running or the elapsed time is greater than 0
            enabled = timerJob != null || elapsedTime > 0
        ) {
            Text("Reset")
        }
    }
}




@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}