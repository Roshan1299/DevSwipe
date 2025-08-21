plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Remove Firebase plugin
    // alias(libs.plugins.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.first.projectswipe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.first.projectswipe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Add this to fix Kapt issues with tests
    kapt {
        correctErrorTypes = true
        useBuildCache = false // Disable for debugging
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE-notice.md",  // Add this line (matches your error)
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "/META-INF/{AL2.0,LGPL2.1}", // For coroutines
                "**/attach_hotspot_windows.dll" // Windows-specific (if needed)
            )
            merges += setOf("META-INF/INDEX.LIST") // Optional: merge instead of exclude
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // ===== REMOVE ALL FIREBASE DEPENDENCIES =====
    // Remove Firebase BOM and all Firebase dependencies:
    // implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // implementation("com.google.android.gms:play-services-auth:21.0.0")
    // implementation("com.google.firebase:firebase-auth-ktx")
    // implementation("com.google.firebase:firebase-firestore-ktx")
    // implementation("com.google.firebase:firebase-storage-ktx")
    // implementation("com.google.android.gms:play-services-auth")

    // ===== ADD NETWORKING DEPENDENCIES =====
    // Retrofit for REST API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for HTTP client and logging
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // ===== KEEP EXISTING DEPENDENCIES =====
    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    // Coroutines (keep existing)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // ===== LIFECYCLE COMPONENTS =====
    // Add lifecycle components for better ViewModel and LiveData support
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // ===== SECURITY =====
    // Add security library for encrypted shared preferences (optional but recommended)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Unit Testing Dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8") // Updated version
    testImplementation("org.robolectric:robolectric:4.11.1") // Updated version
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("androidx.navigation:navigation-testing:2.7.6")

    // Add these for better testing support
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")

    // Android Instrumented Testing Dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2") // or latest stable
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")

    // Debug implementations for testing
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")

    // Add this to avoid kapt issues with testing
    kaptTest("com.github.bumptech.glide:compiler:4.16.0")
    kaptAndroidTest("com.github.bumptech.glide:compiler:4.16.0")
}