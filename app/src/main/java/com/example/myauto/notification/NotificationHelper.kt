package com.example.myauto.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.notification.NotificationReceiver.Companion.ACTION_DONE
import com.example.myauto.notification.NotificationReceiver.Companion.ACTION_SNOOZE
import com.example.myauto.notification.NotificationReceiver.Companion.EXTRA_ITEM_ID
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.utils.FormatterHelper.formatDate
import java.text.SimpleDateFormat

object NotificationHelper {
    const val CHANNEL_ID = "maintenance_notifications"
    const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, item: MaintenanceItemEntity) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(context)

        val notificationType = when {
            item.notificationMileage != null -> "${context.getString(R.string.mileage)} ${item.notificationMileage} ${context.getString(R.string.km)}"
            item.notificationDate != null ->formatDate(item.notificationDate)
            else -> ""
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            item.id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ITEM_ID, item.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val doneIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DONE
            putExtra(EXTRA_ITEM_ID, item.id)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reset_wrench)
            .setContentTitle(context.getString(R.string.notification_title,item.title ?: item.category))
            .setContentText(context.getString(R.string.notification_condition, notificationType))
            .setPriority(PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .addAction(R.drawable.ic_snooze, context.getString(R.string.action_snooze), snoozePendingIntent)
            .addAction(R.drawable.ic_done, context.getString(R.string.action_done), donePendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + item.id, notification)
    }

    fun cancelNotification(context: Context, itemId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID_BASE + itemId)
    }
}