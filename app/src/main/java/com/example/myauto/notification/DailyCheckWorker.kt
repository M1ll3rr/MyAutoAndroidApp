package com.example.myauto.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myauto.AppRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = AppRepository(applicationContext)
        val currentDate = Calendar.getInstance().timeInMillis
        val items = repository.getMaintenanceItemsForDateNotification(currentDate)
        items.forEach {
            NotificationHelper.showNotification(applicationContext, it)
        }
        return Result.success()
    }

    companion object {
        fun schedule(context: Context) {
            val dailyRequest = PeriodicWorkRequestBuilder<DailyCheckWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.DAYS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_maintenance_check",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyRequest
            )
        }
    }
}