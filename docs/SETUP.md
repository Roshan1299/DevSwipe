# Setup Guide

This guide will help you set up the **DevSwipe** Android application locally for development. DevSwipe is a Kotlin-based Android app that helps university students discover side project ideas through a Tinder-style swiping interface, with a Spring Boot backend and PostgreSQL database.

## 📋 Prerequisites

Before you begin, ensure you have the following installed on your development machine:

### Required Software

| Software | Version | Download Link |
|----------|---------|---------------|
| **Android Studio** | Latest Stable (2023.3.1+) | [Download](https://developer.android.com/studio) |
| **Java Development Kit (JDK)** | JDK 17 or higher | [Download](https://adoptium.net/) |
| **Git** | Latest | [Download](https://git-scm.com/) |
| **Android SDK** | API Level 24+ (Android 7.0) | Included with Android Studio |
| **PostgreSQL** | 14+ | [Download](https://www.postgresql.org/download/) |
| **Gradle** | 8.0+ (or use wrapper) | Included with project |

### Android SDK Requirements

- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34

### Backend Requirements

- **Java**: JDK 17+ for Spring Boot backend
- **PostgreSQL**: 14+ for database
- **Maven or Gradle**: For building the backend

### Hardware Requirements

- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 4GB free space for Android Studio + 2GB for project + 1GB for PostgreSQL
- **CPU**: Intel i5 or equivalent, i7+ recommended for optimal performance

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/DevSwipe.git
cd DevSwipe
```

### 2. Setup Backend (Spring Boot + PostgreSQL)

#### 2.1 Install and Configure PostgreSQL
1. Install PostgreSQL from [official website](https://www.postgresql.org/download/)
2. Start the PostgreSQL service
3. Create a database for DevSwipe:
   ```sql
   CREATE DATABASE devswipe_db;
   CREATE USER devswipe_user WITH PASSWORD 'devswipe_password';
   GRANT ALL PRIVILEGES ON DATABASE devswipe_db TO devswipe_user;
   ```

#### 2.2 Configure Backend
1. Navigate to the backend directory: `cd backend`
2. Update the database configuration in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/devswipe_db
   spring.datasource.username=devswipe_user
   spring.datasource.password=devswipe_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.properties.hibernate.format_sql=true
   ```
3. Run the backend application:
   ```bash
   ./gradlew bootRun
   ```
   The backend should start on `http://localhost:8080`

### 3. Setup Android App

#### 3.1 Open in Android Studio
1. Launch Android Studio
2. Click **"Open an Existing Project"**
3. Navigate to the cloned `DevSwipe/code` directory (not the root directory)
4. Click **"OK"** to open the project

#### 3.2 Update Backend URL in Android App
1. Open `code/app/src/main/java/com/first/projectswipe/network/NetworkModule.kt`
2. Update the `BASE_URL` if needed:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8080/" // For Android Emulator
   // For real device, use: "http://YOUR_LOCAL_IP:8080/"
   // For production, use: "https://your-backend-domain.com/"
   ```

### 4. Sync Project
Android Studio will automatically start syncing the project. If it doesn't:
1. Click **"Sync Now"** in the notification bar
2. Or go to **File → Sync Project with Gradle Files**

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
5. Name your AVD: `DevSwipe_Test`
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
// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

// Dependency Injection
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")

// Image Loading
implementation("com.github.bumptech.glide:glide:4.16.0")

// Material Design
implementation("com.google.android.material:material:1.10.0")

// Kotlin Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// JSON
implementation("com.google.code.gson:gson:2.10.1")
```

### Build Configuration

Verify your `code/app/build.gradle` has the correct configuration:

```kotlin
android {
    compileSdk 34

    defaultConfig {
        applicationId "com.first.devswipe"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // DI
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

## 🏃‍♂️ Running the Application

### Method 1: Using Android Studio

1. **Start the backend**: Ensure your Spring Boot backend is running on `http://localhost:8080`
2. Connect your Android device via USB (with USB Debugging enabled)
   - Or start your AVD from AVD Manager
3. Click the **"Run"** button (green play icon)
4. Select your device/emulator
5. Wait for the build to complete and app to launch

### Method 2: Using Command Line

```bash
# Build the app
cd code
./gradlew assembleDebug

# Install on connected device (ensure backend is running first)
./gradlew installDebug

# Run tests
./gradlew test
```

## 🧪 Testing the Setup

### 1. Backend Test
- Start the Spring Boot backend
- Navigate to `http://localhost:8080/actuator/health` to verify the backend is running
- Check that the database tables are created automatically

### 2. App Launch Test
- App should launch without crashes
- You should see the authentication screen
- Authentication requests should reach the backend without errors

### 3. Core Features Test
- Try creating an account with email/password
- Complete post-registration setup (skills & interests)
- Test project swiping interface
- Verify data synchronization with PostgreSQL database

## 🛠️ Troubleshooting

### Common Issues

#### Backend Startup Issues

**Issue**: `java.sql.SQLInvalidAuthorizationSpecException`
```
Solution: Verify PostgreSQL connection details in application.properties
Check database name, username, and password
Ensure PostgreSQL service is running
```

#### Network Issues (Android App)

**Issue**: `java.net.ConnectException: Failed to connect to localhost/127.0.0.1:8080`
```
Solution: Use 10.0.2.2:8080 instead of localhost:8080 for Android emulator
For physical device, use your computer's local IP address
```

#### Build Errors

**Issue**: `Failed to resolve: com.squareup.retrofit2:...`
```bash
Solution: Sync project and check internet connection
cd code
./gradlew clean
./gradlew build
```

**Issue**: `Minimum supported Gradle version is X.X`
```bash
Solution: Update gradle wrapper
cd code
./gradlew wrapper --gradle-version=8.2
```

#### Database Issues

**Issue**: `org.springframework.dao.DataAccessResourceFailureException`
```
Solution: Verify PostgreSQL is running
Check connection details in application.properties
Ensure database user has proper permissions
Run Flyway migrations if needed
```

#### Runtime Issues

**Issue**: `App crashes on startup`
```
Solution: Check Android Studio Logcat for error details
Verify backend is running and accessible
Ensure minimum SDK version (API 24+)
Clear app data and restart
```

### Getting Debug Information

```bash
# View Android app logs
adb logcat | grep DevSwipe

# View backend logs (when running via Gradle)
cd backend
./gradlew bootRun --info

# Clear app data
adb shell pm clear com.first.devswipe

# Check connected devices
adb devices
```

## 🔄 Development Workflow

### 1. Feature Development
```bash
git checkout -b feature/your-feature-name
# Make your changes
git add .
git commit -m "feat: add your feature description"
git push origin feature/your-feature-name
```

### 2. Backend Development
```bash
# Run backend with auto-reload for development
cd backend
./gradlew bootRun --continuous

# Build backend JAR
cd backend
./gradlew build
```

### 3. Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use Android Studio's auto-formatting: `Ctrl+Alt+L` (Windows/Linux) or `Cmd+Option+L` (Mac)
- Follow Spring Boot best practices for backend code

### 4. Testing
- Write unit tests for business logic
- Test API endpoints with Postman or similar tools
- Test on multiple device sizes and API levels
- Test with the actual backend (not just mocks)

## 📚 Additional Resources

### Documentation
- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin for Android](https://developer.android.com/kotlin)
- [Material Design Components](https://material.io/develop/android)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Useful Commands

#### Frontend (Android)
```bash
# Check Gradle version
cd code
./gradlew --version

# Clean build
cd code
./gradlew clean

# Generate signed APK
cd code
./gradlew assembleRelease

# Run lint checks
cd code
./gradlew lint
```

#### Backend (Spring Boot)
```bash
# Run backend
cd backend
./gradlew bootRun

# Build backend
cd backend
./gradlew build

# Run backend tests
cd backend
./gradlew test
```

## 🎯 Next Steps

Once you have the app running:

1. **Start the Backend**: Ensure Spring Boot backend with PostgreSQL is running
2. **Explore the Codebase**: Familiarize yourself with the project structure
3. **Check the MVP Features**: Test authentication, project creation, and swiping
4. **Review Database Schema**: See `DATABASE_SCHEMA.md` for data structure
5. **Read Architecture**: Check `ARCHITECTURE.md` for technical details

## 🆘 Getting Help

If you encounter issues:

1. **Check the Logs**: Android Studio Logcat and backend console provide detailed error information
2. **Database Issues**: Verify PostgreSQL is running and accessible
3. **Network Issues**: Confirm backend is accessible from the Android app
4. **Stack Overflow**: Search for specific error messages
5. **Developer Communities**: [Spring Boot Community](https://spring.io/community), [Android Developer Community](https://developer.android.com/community)

---

**Happy Coding! 🚀**

*Last Updated: October 17, 2025*  
*Version: 3.0*  
*For DevSwipe with Spring Boot + PostgreSQL Development*
