// eu.espcaa.aviator/AviatorApplication.kt

package eu.espcaa.aviator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AviatorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}