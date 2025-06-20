# AndroidPinEntryView

![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![API](https://img.shields.io/badge/API-21%2B-orange.svg)
![Java](https://img.shields.io/badge/Language-Java-blue.svg)
![License](https://img.shields.io/badge/License-MIT-red.svg)
![Version](https://img.shields.io/badge/Version-1.0.0-purple.svg)

**A highly customizable PIN entry view component for Android applications**

*Built with Java for maximum compatibility and stability*

[Features](#features) • [Installation](#installation) • [Usage](#usage) • [Documentation](#documentation) • [Contributing](#contributing)

---

## 🎯 Overview

AndroidPinEntryView is a powerful, flexible, and easy-to-use PIN entry component for Android
applications. Whether you're building authentication flows, OTP verification, or secure PIN entry
screens, this library provides everything you need with a clean, customizable interface.

### 🆕 What's New in 1.0

- **🛡️ Enhanced State Management**: Clean state system for error/success handling
- **⚡ Performance**: Optimized rendering and minimal allocations
- **🎨 Enhanced Customization**: More styling options and better XML support
- **🔄 Smooth Animations**: Built-in state change animations
- **📱 Smart Keyboard**: Intelligent focus and keyboard management
- **🔧 Better Architecture**: Modular design with clear separation of concerns

---

## ✨ Features

### 🎨 **Visual Styles**

- **Rectangle** - Modern card-style with rounded corners
- **Line** - Clean Material Design underlines
- **Circle** - Elegant circular PIN fields
- **None** - Invisible borders for text-only display

### 🔧 **Customization**

- Adjustable item count (any number of PIN digits)
- Flexible dimensions (width, height, spacing, radius)
- Custom colors for borders, text, and backgrounds
- State-specific styling (normal, error, success)
- Password masking with customizable characters

### 🎭 **Animations & States**

- Smooth input animations
- Error shake animation
- Success scaling animation
- State-aware color transitions

### 📱 **User Experience**

- Smart keyboard handling
- Auto-focus management
- Selection menu disabled for security
- Cursor customization
- RTL support

---

## 🚀 Quick Start

### Gradle Setup

**Step 1:** Add JitPack repository to your project's `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2:** Add dependency to your module's `build.gradle`:

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
    app:pinItemCount="6"
    app:pinViewType="rectangle"
    app:pinItemRadius="8dp"
    app:pinLineColor="@color/colorPrimary"
    app:pinAnimationEnabled="true" />
```

#### Java Implementation

```java
PinEntryView pinView = findViewById(R.id.pinEntryView);

// Set completion listener
pinView.setOnPinEnteredListener(new PinEntryView.OnPinEnteredListener() {
    @Override
    public void onPinEntered(String pin) {
        // Handle PIN entry completion
        validatePin(pin);
    }
});

// Handle states
pinView.setErrorState(true); // Show error
pinView.setSuccessState(true); // Show success
```

---

## 📖 Documentation

### 📚 **Module Documentation**

- [Java Implementation Guide](pinentryview-java/README.md)

### 🎨 **Styling Guide**

#### View Types

| Type        | Description                                  | Use Case                       |
|-------------|----------------------------------------------|--------------------------------|
| `rectangle` | Bordered boxes with optional rounded corners | Modern apps, card-style UI     |
| `line`      | Underline style                              | Material Design, minimalist UI |
| `circle`    | Circular PIN fields                          | Unique designs, playful apps   |
| `none`      | Text only, no borders                        | Simple, clean layouts          |

#### XML Attributes

```xml
<!-- Core Configuration -->
<attr name="pinItemCount" format="integer" />          <!-- Number of PIN digits -->
<attr name="pinViewType" format="enum" />              <!-- rectangle|line|circle|none -->
<attr name="pinGravity" format="enum" />               <!-- start|center|end -->

<!-- Dimensions -->
<attr name="pinItemWidth" format="dimension" />        <!-- Width of each field -->
<attr name="pinItemHeight" format="dimension" />       <!-- Height of each field -->
<attr name="pinItemSpacing" format="dimension" />      <!-- Space between fields -->
<attr name="pinItemRadius" format="dimension" />       <!-- Corner radius -->
<attr name="pinLineWidth" format="dimension" />        <!-- Border thickness -->

<!-- Colors -->
<attr name="pinLineColor" format="color" />            <!-- Border color -->
<attr name="pinItemBackgroundColor" format="color" />  <!-- Background color -->
<attr name="pinErrorColor" format="color" />           <!-- Error state color -->
<attr name="pinSuccessColor" format="color" />         <!-- Success state color -->

<!-- Behavior -->
<attr name="pinPasswordHidden" format="boolean" />     <!-- Mask with dots -->
<attr name="pinAutoFocus" format="boolean" />          <!-- Auto show keyboard -->
<attr name="pinAnimationEnabled" format="boolean" />   <!-- Enable animations -->
<attr name="pinCursorVisible" format="boolean" />      <!-- Show cursor -->
```

### 🎬 **Animation Examples**

```java
// Modern state management with PinViewState.Type
pinView.setState(PinViewState.Type.ERROR);    // Error state
pinView.setState(PinViewState.Type.SUCCESS);  // Success state  
pinView.setState(PinViewState.Type.NORMAL);   // Normal state

// Check current state
if (pinView.isInState(PinViewState.Type.ERROR)) {
    // Handle error state
}

// Configure state-specific animations
pinView.setStateAnimationEnabled(PinViewState.Type.ERROR, true);   // Enable shake
pinView.setStateAnimationEnabled(PinViewState.Type.SUCCESS, true); // Enable scale

// Configure state-specific colors
pinView.setStateLineColor(PinViewState.Type.ERROR, Color.RED);
pinView.setStateTextColor(PinViewState.Type.ERROR, Color.RED);
pinView.setStateBackgroundColor(PinViewState.Type.ERROR, Color.parseColor("#FFEBEE"));
```

---

## 📱 Examples

### Basic 4-Digit PIN

```xml
<com.rorpheeyah.java.pinentryview.PinEntryView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pinItemCount="4"
    app:pinViewType="rectangle"
    app:pinItemRadius="4dp" />
```

### OTP Verification (6 digits)

```xml
<com.rorpheeyah.java.pinentryview.PinEntryView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pinItemCount="6"
    app:pinViewType="line"
    app:pinLineColor="@color/colorAccent"
    app:pinAnimationEnabled="true" />
```

### Secure Password Entry

```xml
<com.rorpheeyah.java.pinentryview.PinEntryView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pinItemCount="8"
    app:pinViewType="circle"
    app:pinPasswordHidden="true"
    app:pinAutoFocus="true" />
```

---

## 🛠️ Advanced Configuration

### Custom Styling

```java
// Configure dimensions
pinView.setItemWidth(dpToPx(50));
pinView.setItemHeight(dpToPx(60));
pinView.setItemSpacing(dpToPx(8));
pinView.setItemRadius(dpToPx(16));
pinView.setLineWidth(dpToPx(4));

// Configure colors
pinView.setLineColor(Color.BLUE);
pinView.setItemBackgroundColor(Color.LTGRAY);

// Configure state-specific styling
pinView.setStateLineColor(PinViewState.Type.ERROR, Color.parseColor("#F44336"));
pinView.setStateLineColor(PinViewState.Type.SUCCESS, Color.parseColor("#4CAF50"));
```

### State Management

```java
// Check current state
switch (pinView.getState()) {
    case NORMAL:
        // Handle normal state
        break;
    case ERROR:
        // Handle error state
        break;
    case SUCCESS:
        // Handle success state
        break;
}

// Configure animations per state
pinView.setStateAnimationEnabled(PinViewState.Type.ERROR, true);   // Shake on error
pinView.setStateAnimationEnabled(PinViewState.Type.SUCCESS, true); // Scale on success
```

### Comprehensive Example

```java
public class MainActivity extends AppCompatActivity {
    private PinEntryView pinView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        pinView = findViewById(R.id.pinEntryView);
        
        // Configure appearance
        pinView.setItemCount(6);
        pinView.setViewType(PinEntryView.VIEW_TYPE_RECTANGLE);
        pinView.setItemRadius(dpToPx(8));
        pinView.setAnimationEnabled(true);
        
        // Configure states
        pinView.setStateLineColor(PinViewState.Type.ERROR, Color.parseColor("#F44336"));
        pinView.setStateLineColor(PinViewState.Type.SUCCESS, Color.parseColor("#4CAF50"));
        
        // Enable animations
        pinView.setStateAnimationEnabled(PinViewState.Type.ERROR, true);
        pinView.setStateAnimationEnabled(PinViewState.Type.SUCCESS, true);
        
        // Set listener
        pinView.setOnPinEnteredListener(pin -> {
            if (isValidPin(pin)) {
                pinView.setState(PinViewState.Type.SUCCESS);
                // Proceed with authentication
                proceedWithAuth(pin);
            } else {
                pinView.setState(PinViewState.Type.ERROR);
                // Show error message
                showErrorMessage();
            }
        });
    }
    
    private boolean isValidPin(String pin) {
        // Your validation logic here
        return pin.equals("123456");
    }
    
    private void proceedWithAuth(String pin) {
        // Your authentication logic here
    }
    
    private void showErrorMessage() {
        // Show error to user
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
```

---

## 📄 Documentation Reference

For complete API documentation, see: [Java Implementation Guide](pinentryview-java/README.md)

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### 🐛 **Bug Reports**

- Use the [issue tracker](https://github.com/rorpheeyah/AndroidPinEntryView/issues)
- Include Android version, device model, and reproduction steps
- Provide sample code if possible

### 💡 **Feature Requests**

- Check
  existing [feature requests](https://github.com/rorpheeyah/AndroidPinEntryView/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement)
- Describe the use case and expected behavior

### 🔧 **Pull Requests**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests if applicable
5. Update documentation
6. Commit changes (`git commit -m 'Add amazing feature'`)
7. Push to branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

### 📋 **Development Setup**

```bash
git clone https://github.com/rorpheeyah/AndroidPinEntryView.git
cd AndroidPinEntryView
./gradlew build
```

**Requirements:**

- Android Studio Arctic Fox or later
- JDK 17+ (for latest AGP)
- Android SDK API 21+

---

## 📄 License

```
MIT License

Copyright (c) 2025 rorpheeyah

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgments

- Inspired by Material Design PIN entry patterns
- Built with modern Android development practices
- Community feedback and contributions
- Open source libraries and tools used

---

**⭐ If this library helps you, please give it a star!**

[Report Bug](https://github.com/rorpheeyah/AndroidPinEntryView/issues) • [Request Feature](https://github.com/rorpheeyah/AndroidPinEntryView/issues) • [Discussions](https://github.com/rorpheeyah/AndroidPinEntryView/discussions)

Made with ❤️ for the Android community
