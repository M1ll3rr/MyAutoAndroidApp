package com.example.myauto.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.myauto.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_SNOOZE = "com.example.myauto.ACTION_SNOOZE_MAINTENANCE"
        const val ACTION_DONE = "com.example.myauto.ACTION_DONE_MAINTENANCE"
        const val EXTRA_ITEM_ID = "item_id"
        const val SNOOZE_DELAY_DAYS = 1L
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val itemId = intent.getIntExtra(EXTRA_ITEM_ID, -1)
        if (itemId == -1) return

        when (action) {
            ACTION_SNOOZE -> scheduleSnooze(context, itemId)
            ACTION_DONE -> markAsDone(context, itemId)
        }

        NotificationHelper.cancelNotification(context, itemId)
    }

    private fun scheduleSnooze(context: Context, itemId: Int) {
        val workRequest = OneTimeWorkRequestBuilder<SnoozeWorker>()
            .setInputData(workDataOf("item_id" to itemId))
            .setInitialDelay(SNOOZE_DELAY_DAYS, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun markAsDone(context: Context, itemId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = AppRepository(context)
            repository.updateMaintenanceNotificationStatus(itemId, null, null)
        }
    }
}