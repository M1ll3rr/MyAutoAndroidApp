package com.example.myauto.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myauto.AppRepository
import java.util.Calendar

class SnoozeWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val ONE_DAY_MS = 86400000L

    override suspend fun doWork(): Result {
        val itemId = inputData.getInt("item_id", -1)
        if (itemId == -1) return Result.failure()

        val repository = AppRepository(applicationContext)
        val item = repository.getMaintenanceItemById(itemId)

        item?.let {
            val newDate = Calendar.getInstance().timeInMillis + ONE_DAY_MS
            repository.updateMaintenanceNotificationStatus(itemId, it.notificationMileage, newDate)
            NotificationHelper.showNotification(applicationContext, it)
        }

        return Result.success()
    }
}