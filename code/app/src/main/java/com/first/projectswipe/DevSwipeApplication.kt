// DevSwipeApplication.kt
package com.first.projectswipe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DevSwipeApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}