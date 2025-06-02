package com.example.myauto

import android.app.Application
import com.example.myauto.data.PreferencesManager
import com.example.myauto.notification.DailyCheckWorker
import com.example.myauto.notification.NotificationHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager.initialize(applicationContext)
        NotificationHelper.createNotificationChannel(this)
        DailyCheckWorker.schedule(this)
    }
}