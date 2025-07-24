# Firebase Setup Guide

## 🔥 Overview

ProjectSwipe uses Firebase as the backend service for the MVP version. This guide will walk you through setting up Firebase Authentication, Firestore Database, and Cloud Messaging for the project.

## 📋 Prerequisites

- Google account
- Android Studio installed
- ProjectSwipe project cloned locally
- Basic understanding of Firebase console

## 🚀 Step 1: Create Firebase Project

### 1.1 Go to Firebase Console
1. Visit [Firebase Console](https://console.firebase.google.com/)
2. Click **"Create a project"** or **"Add project"**

### 1.2 Project Configuration
1. **Project name**: Enter `ProjectSwipe` (or your preferred name)
2. **Google Analytics**: Enable for better insights (recommended)
3. **Analytics account**: Use default or create new
4. Click **"Create project"** and wait for setup completion

### 1.3 Add Android App
1. Click **"Add app"** and select Android icon
2. **Android package name**: Enter your app's package name
   ```
   com.yourpackage.projectswipe
   ```
3. **App nickname**: `ProjectSwipe Android` (optional but recommended)  
4. **SHA-1 certificate**: Add your debug keystore SHA-1
   ```bash
   # Get debug keystore SHA-1 (run in terminal)
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
   # Default password: android
   ```
5. Click **"Register app"**

### 1.4 Download Configuration File
1. Download `google-services.json`
2. Place it in your `app/` directory (same level as `build.gradle`)
3. **Important**: Never commit this file to public repositories

## 🔐 Step 2: Setup Firebase Authentication

### 2.1 Enable Authentication
1. In Firebase Console, go to **"Authentication"**
2. Click **"Get started"**
3. Go to **"Sign-in method"** tab

### 2.2 Configure Email/Password Authentication
1. Click **"Email/Password"**
2. **Enable** the first option (Email/Password)
3. **Optional**: Enable "Email link (passwordless sign-in)" if desired
4. Click **"Save"**

### 2.3 Configure Google Sign-In
1. Click **"Google"** from providers list
2. **Enable** Google sign-in
3. **Project support email**: Select your email
4. **Web SDK configuration**: 
   - **Web client ID**: Will be auto-generated
   - Copy this for later use in Android setup
5. Click **"Save"**

### 2.4 Add SHA-1 for Google Sign-In
1. Go to **"Project settings"** (gear icon)
2. Scroll to **"Your apps"** section
3. Click on your Android app
4. Add SHA-1 certificate fingerprints:
   ```bash
   # Debug keystore
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
   
   # Release keystore (when ready for production)
   keytool -list -v -alias <your-key-alias> -keystore <path-to-your-keystore>
   ```

## 🗄️ Step 3: Setup Firestore Database

### 3.1 Create Firestore Database
1. Go to **"Firestore Database"**
2. Click **"Create database"**
3. **Security rules**: Start in **"Test mode"** (we'll secure later)
4. **Location**: Choose closest to your users (e.g., `us-central` for North America)
5. Click **"Done"**

### 3.2 Configure Security Rules (Development)
```javascript
// Development rules (NOT for production)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Allow authenticated users to read all projects
    match /projects/{projectId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == resource.data.createdBy;
      allow create: if request.auth != null;
    }
    
    // Allow authenticated users to read/write user profiles
    match /userProfiles/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 3.3 Create Initial Collections Structure
Create these collections manually or let the app create them:

1. **users** - User authentication data
2. **projects** - Project ideas and details  
3. **userProfiles** - Extended user information (skills, interests)

## 🔔 Step 4: Setup Cloud Messaging (FCM)

### 4.1 Enable Cloud Messaging
1. Go to **"Cloud Messaging"**
2. Cloud Messaging is automatically enabled for new projects
3. Note the **Server key** for future backend integration

### 4.2 Configure Notification Channels (Optional)
```kotlin
// Android app will handle notification channels
// No additional Firebase console setup required for MVP
```

## ⚙️ Step 5: Android Project Configuration

### 5.1 Add Firebase Dependencies
Add to your `app/build.gradle`:

```kotlin
dependencies {
    // Firebase BoM (Bill of Materials)
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    
    // Firebase services
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-messaging-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'
    
    // Google Sign-In
    implementation 'com.google.android.gms:play-services-auth:20.7.0'
}
```

### 5.2 Apply Google Services Plugin
In your `app/build.gradle`:
```kotlin
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.gms.google-services'  // Add this line
}
```

In your project-level `build.gradle`:
```kotlin
dependencies {
    classpath 'com.google.gms:google-services:4.4.0'  // Add this line
}
```

### 5.3 Initialize Firebase in Application Class
```kotlin
class ProjectSwipeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase is automatically initialized
        // No manual initialization required
    }
}
```

## 🔧 Step 6: Environment Configuration

### 6.1 Create Firebase Configuration Class
```kotlin
object FirebaseConfig {
    // These will be automatically loaded from google-services.json
    const val PROJECT_ID = "your-project-id"
    const val WEB_CLIENT_ID = "your-web-client-id.apps.googleusercontent.com"
}
```

### 6.2 Google Sign-In Configuration
```kotlin
// In your AuthViewModel or Repository
private fun configureGoogleSignIn(): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id)) // Auto-generated
        .requestEmail()
        .build()
    
    return GoogleSignIn.getClient(this, gso)
}
```

## 🧪 Step 7: Testing Firebase Setup

### 7.1 Test Authentication
```kotlin
// Test email/password signup
FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

// Test Google Sign-In
val signInIntent = googleSignInClient.signInIntent
startActivityForResult(signInIntent, RC_SIGN_IN)
```

### 7.2 Test Firestore Connection
```kotlin
// Test writing to Firestore
val db = FirebaseFirestore.getInstance()
val testData = hashMapOf("test" to "data")

db.collection("test")
    .add(testData)
    .addOnSuccessListener { 
        Log.d("Firebase", "Test document added successfully") 
    }
```

### 7.3 Test FCM
```kotlin
// Test getting FCM token
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (!task.isSuccessful) {
        Log.w("FCM", "Fetching FCM registration token failed", task.exception)
        return@addOnCompleteListener
    }

    val token = task.result
    Log.d("FCM", "FCM Registration Token: $token")
}
```

## 🔒 Step 8: Production Security Setup

### 8.1 Secure Firestore Rules
```javascript
// Production-ready security rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own user document
    match /users/{userId} {
      allow read, write: if request.auth != null 
        && request.auth.uid == userId
        && validateUserData(request.resource.data);
    }
    
    // Projects are readable by all authenticated users
    // But only writable by the creator
    match /projects/{projectId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null 
        && request.auth.uid == request.resource.data.createdBy
        && validateProjectData(request.resource.data);
      allow update, delete: if request.auth != null 
        && request.auth.uid == resource.data.createdBy;
    }
    
    // User profiles are private to the user
    match /userProfiles/{userId} {
      allow read, write: if request.auth != null 
        && request.auth.uid == userId;
    }
    
    // Validation functions
    function validateUserData(userData) {
      return userData.keys().hasAll(['email', 'displayName', 'createdAt'])
        && userData.email is string
        && userData.displayName is string;
    }
    
    function validateProjectData(projectData) {
      return projectData.keys().hasAll(['title', 'description', 'difficulty', 'createdBy', 'createdAt'])
        && projectData.title is string
        && projectData.description is string
        && projectData.difficulty in ['Beginner', 'Intermediate', 'Advanced'];
    }
  }
}
```

## 🔍 Step 9: Monitoring and Analytics

### 9.1 Enable Firebase Analytics
Analytics is automatically enabled if you selected it during project creation.

### 9.2 Setup Crashlytics (Optional but Recommended)
```kotlin
// Add to app/build.gradle
implementation 'com.google.firebase:firebase-crashlytics-ktx'

// Add plugin
id 'com.google.firebase.crashlytics'
```

## ❌ Troubleshooting

### Common Issues

**🚫 Google Sign-In fails**
- Ensure SHA-1 certificate is correctly added
- Check package name matches exactly
- Verify `google-services.json` is in correct location

**🚫 Firestore permission denied**
- Check security rules allow your operation
- Ensure user is authenticated
- Verify collection/document path is correct

**🚫 FCM notifications not working**
- Check if app is in foreground/background
- Verify FCM token is being generated
- Test with Firebase Console test message

### Debug Commands
```bash
# Check Firebase project configuration
./gradlew app:dependencies | grep firebase

# Verify google-services.json
cat app/google-services.json | grep project_id

# Check SHA-1 certificate
keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
```

## ✅ Verification Checklist

- [ ] Firebase project created
- [ ] `google-services.json` downloaded and placed correctly
- [ ] Authentication providers enabled (Email/Password + Google)
- [ ] SHA-1 certificates added
- [ ] Firestore database created with proper rules
- [ ] Firebase dependencies added to `build.gradle`
- [ ] Google Services plugin applied
- [ ] Test authentication working
- [ ] Test Firestore read/write working
- [ ] FCM token generation working

Your Firebase setup is now complete! 🎉
