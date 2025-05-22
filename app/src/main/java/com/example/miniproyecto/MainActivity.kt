package com.example.miniproyecto

import android.content.Context
import android.hardware.*
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.miniproyecto.ui.theme.MiniProyectoTheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity(), SensorEventListener {
    private var isFlashOn = false
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var isSOSActive = false
    private var sosJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.firstOrNull()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mediaPlayer = MediaPlayer.create(this, R.raw.sos_bip)

        setContent {
            MiniProyectoTheme {
                FlashlightApp(
                    onToggleFlash = { toggleFlashLight() },
                    onSOS = { toggleSOS() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = acceleration - SensorManager.GRAVITY_EARTH

            if (delta > 8.0) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime
                    toggleFlashLight()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun toggleFlashLight() {
        try {
            cameraId?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isFlashOn = !isFlashOn
                    cameraManager.setTorchMode(it, isFlashOn)
                    vibratePhone()
                } else {
                    Toast.makeText(this, "Linterna no soportada en esta versión", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al controlar la linterna", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleSOS() {
        isSOSActive = !isSOSActive
        if (isSOSActive) {
            sosJob = CoroutineScope(Dispatchers.Main).launch {
                while (isSOSActive) {
                    playerSOSBeep()
                    flashSOS()
                    delay(3000)
                }
            }
        } else {
            sosJob?.cancel()
            mediaPlayer?.stop()
            mediaPlayer?.prepare()
        }
    }


    private suspend fun flashSOS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val shortDelay = 200L
            val longDelay = 600L
            val pause = 400L

            val sequence = listOf(
                shortDelay, shortDelay, shortDelay, pause,
                longDelay, longDelay, longDelay, pause,
                shortDelay, shortDelay, shortDelay
            )

            cameraId?.let {
                for (time in sequence) {
                    if (!isSOSActive) return


                    cameraManager.setTorchMode(it, true)
                    playerSOSBeep()
                    delay(time)


                    cameraManager.setTorchMode(it, false)
                    delay(shortDelay)
                }
            }
        } else {
            Toast.makeText(this, "La linterna no es compatible con esta versión de Android", Toast.LENGTH_SHORT).show()
        }
    }


    private fun playerSOSBeep() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
            it.start()
        }
    }



    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val duration = if (isFlashOn) 200L else 100L

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}


@Composable
fun FlashlightApp(onToggleFlash: () -> Unit, onSOS: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onToggleFlash,
            modifier = Modifier
                .size(width = 250.dp, height = 60.dp)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(text = "Encender/Apagar Linterna", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSOS,
            modifier = Modifier
                .size(width = 250.dp, height = 200.dp)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sos),
                contentDescription = "SOS"
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewFlashlightApp() {
    MiniProyectoTheme {
        FlashlightApp({}, {})
    }
}
