package com.rorpheeyah.androidpinentryview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.rorpheeyah.java.pinentryview.PinEntryView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Only apply system bar insets, ignore IME
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            // Return modified insets without consuming IME insets
            WindowInsetsCompat.Builder(insets)
                .setInsets(WindowInsetsCompat.Type.ime(), Insets.NONE)
                .build()
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Setup pin entry view with error handling
        val pinEntryView = findViewById<PinEntryView>(R.id.pinEntryView)
        pinEntryView.setSuccessColor("#4CAF50".toColorInt())
        pinEntryView.setOnPinEnteredListener { pin ->
            // Example validation - in a real app, you would validate against a real PIN
            if (pin != "1234") {
                pinEntryView.setErrorState(true)
                pinEntryView.setSuccessState(false)
            } else {
                pinEntryView.setErrorState(false)
                pinEntryView.setSuccessState(true)
            }
        }

        val pinEntryView1 = findViewById<PinEntryView>(R.id.pinEntryView1)
        pinEntryView1.setOnPinEnteredListener { pin ->
            // Example validation - in a real app, you would validate against a real PIN
            if (pin != "5678") {
                pinEntryView1.setErrorState(true)
                pinEntryView1.setSuccessState(false)
            } else {
                pinEntryView1.setErrorState(false)
                pinEntryView1.setSuccessState(true)
            }
        }

        // Setup circle pin view with different error handling example
        val circlePinView = findViewById<PinEntryView>(R.id.pinEntryView3)
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
                circlePinView.setSuccessState(false)
            } else {
                circlePinView.setErrorState(false)
                circlePinView.setSuccessAnimationEnabled(true)
            }
        }
    }
}