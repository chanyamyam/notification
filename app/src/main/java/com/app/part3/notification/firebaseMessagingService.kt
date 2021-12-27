package com.app.part3.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class firebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        createNotificationChannel()

        val type = p0.data["type"]?.let {
            NotificationType.valueOf(it)
        }
        val title = p0.data["title"]
        val message = p0.data["message"]

        type ?: return


        NotificationManagerCompat.from(this)
            .notify(type.id,createNotification(type,title,message))

    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {
        val intent = Intent(this,MainActivity::class.java).apply{
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)



        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_none_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when(type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "☹\uD83D\uDE1D\uD83D\uDE00\uD83D\uDE03\uD83D\uDE06\uD83D\uDE04\uD83D\uDE05\uD83E\uDD17\uD83D\uDE2F\uD83D\uDE2F\uD83E\uDD73\uD83D\uDE05\uD83D\uDE01\uD83D\uDE0E\uD83E\uDD14\uD83E\uDD2D\uD83D\uDE2F\uD83E\uDD2E\uD83E\uDD10\uD83D\uDE2B\uD83D\uDE2E\u200D\uD83D\uDCA8\uD83E\uDD12\uD83D\uDE32\uD83E\uDD74\uD83D\uDE2C\uD83C\uDF83\uD83D\uDE40\uD83E\uDD2E\uD83D\uDE24\uD83E\uDD15\uD83E\uDD10\uD83D\uDE16\uD83D\uDE11\uD83E\uDD25\uD83D\uDE08\uD83D\uDE1E\uD83D\uDC7F\uD83D\uDE25\uD83D\uDE1F\uD83D\uDE1A\uD83D\uDE36\u200D\uD83C\uDF2B️"
                        )
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            setTextViewText(R.id.title,title)
                            setTextViewText(R.id.message,message)
                        }
                    )
            }
        }
        return notificationBuilder.build()
    }
    companion object {
        private const val CHANNEL_NAME = "Emoji party"
        private const val CHANNEL_DESCRIPTION = "Emoji party 채널"
        private const val CHANNEL_ID = "channel_id"
    }
}