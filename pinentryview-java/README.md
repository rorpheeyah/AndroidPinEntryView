# PinEntryView - Java Implementation

![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)
![Stability](https://img.shields.io/badge/Stability-Production%20Ready-green.svg)
![Tests](https://img.shields.io/badge/Tests-Covered-blue.svg)

**Stable, production-ready Java implementation of PinEntryView**

*The tried-and-tested choice for reliable PIN entry functionality*

---

## 🎯 Overview

The Java implementation of PinEntryView provides a robust, well-tested solution for PIN entry
functionality in Android applications. Built with traditional Android development patterns, it
offers maximum compatibility and stability for production applications.

### ✅ **Why Choose Java Implementation?**

- **🏗️ Production Proven**: Extensively tested in real-world applications
- **🔄 Backward Compatible**: Works with older Android versions and codebases
- **📚 Well Documented**: Comprehensive JavaDoc and examples
- **🛡️ Stable API**: Minimal breaking changes, predictable behavior
- **🔗 Easy Integration**: Drop-in replacement for existing EditText components

---

## 🚀 Quick Start

### Installation

Add to your module's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.rorpheeyah:AndroidPinEntryView:1.0.0'
}
```

### Basic Usage

#### XML Layout

```xml
<com.rorpheeyah.java.pinentryview.PinEntryView
    android:id="@+id/pinEntryView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    android:inputType="number"
    app:itemCount="4"
    app:viewType="rectangle"
    app:itemRadius="8dp"
    app:lineColor="@color/colorPrimary"
    app:autoFocus="true" />
```

#### Java Code

```java
public class MainActivity extends AppCompatActivity {
    private PinEntryView pinEntryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        pinEntryView = findViewById(R.id.pinEntryView);
        setupPinEntry();
    }

    private void setupPinEntry() {
        // Set completion listener
        pinEntryView.setOnPinEnteredListener(new PinEntryView.OnPinEnteredListener() {
            @Override
            public void onPinEntered(String pin) {
                validatePin(pin);
            }
        });
    }

    private void validatePin(String pin) {
        if ("1234".equals(pin)) {
            // Show success
            pinEntryView.setSuccessState(true);
            Toast.makeText(this, "PIN Correct!", Toast.LENGTH_SHORT).show();
        } else {
            // Show error with shake animation
            pinEntryView.setErrorState(true);
            Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
            
            // Clear after 2 seconds
            new Handler().postDelayed(() -> {
                pinEntryView.setText("");
                pinEntryView.setErrorState(false);
            }, 2000);
        }
    }
}
```

