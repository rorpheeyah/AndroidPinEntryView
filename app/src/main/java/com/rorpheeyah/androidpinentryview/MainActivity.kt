package com.rorpheeyah.androidpinentryview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.rorpheeyah.androidpinentryview.R
import com.rorpheeyah.java.pinentryview.PinEntryView
import com.rorpheeyah.java.pinentryview.PinViewState
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Apply padding for system bars
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                // Use the larger of system bar bottom or IME height
                max(systemBars.bottom, ime.bottom)
            )

            // Consume the insets
            WindowInsetsCompat.CONSUMED
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Setup pin entry view with new clean state API
        val pinEntryView = findViewById<PinEntryView>(R.id.pinEntryView)
        pinEntryView.setOnPinEnteredListener { pin ->
            if (pin != "1234") {
                pinEntryView.setState(PinViewState.Type.ERROR)
            } else {
                pinEntryView.setState(PinViewState.Type.SUCCESS)
            }
        }

        val pinEntryView1 = findViewById<PinEntryView>(R.id.pinEntryView1)

        // BACKWARD COMPATIBLE WAY - Still works for existing code
        pinEntryView1.setOnPinEnteredListener { pin ->
            if (pin != "5678") {
                pinEntryView1.setErrorState(true)
            } else {
                pinEntryView1.setSuccessState(true)
            }
        }

        // Setup circle pin view with different error handling example
        val circlePinView = findViewById<PinEntryView>(R.id.pinEntryView3)

        // Configure success animation for this view
        circlePinView.setStateAnimationEnabled(PinViewState.Type.SUCCESS, true)

        circlePinView.setOnPinEnteredListener { pin ->
            // Example: Check if PIN has sequential digits (e.g., 1234, 4567)
            var isSequential = true
            for (i in 0 until pin.length - 1) {
                if (pin[i].digitToInt() + 1 != pin[i + 1].digitToInt()) {
                    isSequential = false
                    break
                }
            }

            if (isSequential) {
                circlePinView.setState(PinViewState.Type.ERROR)
            } else {
                circlePinView.setState(PinViewState.Type.SUCCESS)
            }
        }
    }
}
