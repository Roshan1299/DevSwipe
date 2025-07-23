# Setup Guide

This guide will help you set up the **ProjectSwipe** Android application locally for development. ProjectSwipe is a Kotlin-based Android app that helps university students discover side project ideas through a Tinder-style swiping interface.

## 📋 Prerequisites

Before you begin, ensure you have the following installed on your development machine:

### Required Software

| Software | Version | Download Link |
|----------|---------|---------------|
| **Android Studio** | Latest Stable (2023.3.1+) | [Download](https://developer.android.com/studio) |
| **Java Development Kit (JDK)** | JDK 11 or higher | [Download](https://adoptium.net/) |
| **Git** | Latest | [Download](https://git-scm.com/) |
| **Android SDK** | API Level 24+ (Android 7.0) | Included with Android Studio |

### Android SDK Requirements

- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34

### Hardware Requirements

- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 4GB free space for Android Studio + 2GB for project
- **CPU**: Intel i5 or equivalent, i7+ recommended for optimal performance

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/ProjectSwipe.git
cd ProjectSwipe
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click **"Open an Existing Project"**
3. Navigate to the cloned `ProjectSwipe` directory
4. Click **"OK"** to open the project

### 3. Sync Project

Android Studio will automatically start syncing the project. If it doesn't:
1. Click **"Sync Now"** in the notification bar
2. Or go to **File → Sync Project with Gradle Files**

## 🔥 Firebase Configuration

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Create a project"** or **"Add project"**
3. Enter project name: `ProjectSwipe` (or your preferred name)
4. **Disable** Google Analytics (optional for development)
5. Click **"Create project"**

### Step 2: Add Android App to Firebase

1. In Firebase Console, click **"Add app"** → **Android**
2. Enter the following details:
   ```
   Android package name: com.yourname.projectswipe
   App nickname: ProjectSwipe
   Debug signing certificate SHA-1: (optional for development)
   ```
3. Click **"Register app"**

### Step 3: Download Configuration File

1. Download the `google-services.json` file
2. Place it in your app module directory:
   ```
   ProjectSwipe/
   └── app/
       └── google-services.json  ← Place here
   ```

### Step 4: Enable Firebase Services

In Firebase Console, enable the following services:

#### Authentication
1. Go to **Authentication → Sign-in method**
2. Enable **Email/Password**
3. Enable **Google** sign-in
   - Add your app's SHA-1 fingerprint (for release builds)
   - Download updated `google-services.json` if prompted

#### Firestore Database
1. Go to **Firestore Database**
2. Click **"Create database"**
3. Select **"Start in test mode"** (for development)
4. Choose your preferred location (closest to your users)

#### Cloud Messaging (FCM)
1. Go to **Cloud Messaging**
2. FCM is automatically enabled when you add the Android app

### Step 5: Get SHA-1 Fingerprint (For Google Sign-In)

Run this command in your project terminal:

```bash
# For debug builds (development)
./gradlew signingReport

# Or use keytool directly
keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
```

Default password for debug keystore: `android`

Copy the SHA-1 fingerprint and add it to your Firebase project:
1. Go to **Project Settings → General**
2. Scroll to **Your apps** → **ProjectSwipe**
3. Click **"Add fingerprint"**
4. Paste the SHA-1 fingerprint

## 📱 Android Studio Configuration

### SDK Manager Setup

1. Open **Tools → SDK Manager**
2. Ensure the following are installed:
   - **Android SDK Platform 34** (API Level 34)
   - **Android SDK Platform-Tools** (latest)
   - **Android SDK Build-Tools** (34.0.0 or latest)
   - **Google Play Services**
   - **Google Repository**

### AVD (Android Virtual Device) Setup

1. Open **Tools → AVD Manager**
2. Click **"Create Virtual Device"**
3. Choose a device (recommended: **Pixel 6** or **Pixel 7**)
4. Select system image: **API 34** (Android 14)
5. Name your AVD: `ProjectSwipe_Test`
6. Click **"Finish"**

### Gradle Configuration

Ensure your `gradle.properties` file contains:

```properties
# Android
android.useAndroidX=true
android.enableJetifier=true

# Kotlin
kotlin.code.style=official

# Performance
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

## 🔧 Dependencies and Build Configuration

### Key Dependencies (Already in build.gradle)

The project uses the following major dependencies:

```kotlin
// Firebase
implementation 'com.google.firebase:firebase-auth-ktx:22.3.0'
implementation 'com.google.firebase:firebase-firestore-ktx:24.10.0'
implementation 'com.google.firebase:firebase-messaging-ktx:23.4.0'

// Google Sign-In
implementation 'com.google.android.gms:play-services-auth:20.7.0'

// Material Design
implementation 'com.google.android.material:material:1.11.0'

// Kotlin Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// ViewPager2 (for swipe functionality)
implementation 'androidx.viewpager2:viewpager2:1.0.0'
```

### Build Configuration

Verify your `app/build.gradle` has the correct configuration:

```kotlin
android {
    compileSdk 34

    defaultConfig {
        applicationId "com.yourname.projectswipe"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = '11'
    }

    buildFeatures {
        viewBinding true
    }
}
```

## 🏃‍♂️ Running the Application

### Method 1: Using Android Studio

1. Connect your Android device via USB (with USB Debugging enabled)
   - Or start your AVD from AVD Manager
2. Click the **"Run"** button (green play icon)
3. Select your device/emulator
4. Wait for the build to complete and app to launch

### Method 2: Using Command Line

```bash
# Build the app
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## 🧪 Testing the Setup

### 1. App Launch Test
- App should launch without crashes
- You should see the authentication screen

### 2. Firebase Connection Test
- Try creating an account with email/password
- Verify user appears in Firebase Console → Authentication

### 3. Google Sign-In Test
- Try signing in with Google
- Verify SHA-1 fingerprint is correctly configured if this fails

### 4. Database Test
- Complete post-registration setup (skills & interests)
- Verify data appears in Firebase Console → Firestore

## 🛠️ Troubleshooting

### Common Issues

#### Build Errors

**Issue**: `Failed to resolve: com.google.firebase:firebase-***`
```bash
Solution: Sync project and check internet connection
./gradlew clean
./gradlew build
```

**Issue**: `Minimum supported Gradle version is X.X`
```bash
Solution: Update gradle wrapper
./gradlew wrapper --gradle-version=8.2
```

#### Firebase Issues

**Issue**: `FirebaseApp is not initialized`
```
Solution: Ensure google-services.json is in app/ directory
Check that google-services plugin is applied in build.gradle
```

**Issue**: `Google Sign-In failed`
```
Solution: Verify SHA-1 fingerprint is added to Firebase project
Ensure Google Sign-In is enabled in Firebase Console
```

#### Runtime Issues

**Issue**: `App crashes on startup`
```
Solution: Check Android Studio Logcat for error details
Verify minimum SDK version (API 24+)
Clear app data and restart
```

### Getting Debug Information

```bash
# View detailed logs
adb logcat | grep ProjectSwipe

# Clear app data
adb shell pm clear com.yourname.projectswipe

# Check connected devices
adb devices
```

## 🔄 Development Workflow

### 1. Feature Development
```bash
git checkout -b feature/your-feature-name
# Make your changes
git add .
git commit -m "Add: your feature description"
git push origin feature/your-feature-name
```

### 2. Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use Android Studio's auto-formatting: `Ctrl+Alt+L` (Windows/Linux) or `Cmd+Option+L` (Mac)

### 3. Testing
- Write unit tests for business logic
- Test on multiple device sizes and API levels
- Test both online and offline scenarios

## 📚 Additional Resources

### Documentation
- [Android Developer Guide](https://developer.android.com/guide)
- [Firebase Android Documentation](https://firebase.google.com/docs/android/setup)
- [Kotlin for Android](https://developer.android.com/kotlin)
- [Material Design Components](https://material.io/develop/android)

### Useful Commands
```bash
# Check Gradle version
./gradlew --version

# Clean build
./gradlew clean

# Generate signed APK
./gradlew assembleRelease

# Run lint checks
./gradlew lint
```

## 🎯 Next Steps

Once you have the app running:

1. **Explore the Codebase**: Familiarize yourself with the project structure
2. **Check the MVP Features**: Test authentication, project creation, and swiping
3. **Review Database Schema**: See `DATABASE_SCHEMA.md` for data structure
4. **Read Architecture**: Check `ARCHITECTURE.md` for technical details
5. **Firebase Setup**: Review `FIREBASE_SETUP.md` for advanced configuration

## 🆘 Getting Help

If you encounter issues:

1. **Check the Logs**: Android Studio Logcat provides detailed error information
2. **Firebase Console**: Verify your configuration and check for service issues
3. **Stack Overflow**: Search for specific error messages
4. **Android Developer Community**: [developer.android.com/community](https://developer.android.com/community)

---

**Happy Coding! 🚀**

*Last Updated: July 23, 2025*  
*Version: 1.0*  
*For ProjectSwipe MVP Development*
