package com.example.lokanala.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lokanala.MainActivity
import com.example.lokanala.R
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.dataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "promo_channel"
        const val CHANNEL_NAME = "Promo Notifications"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 🔑 Ambil target user dari FCM data payload
        val targetUserId = message.data["targetUserId"] ?: return

        // 🔐 Ambil user yang sedang login dari DataStore
        val currentUserId = getCurrentUserId() ?: return

        // 🚫 BUKAN MILIK USER YANG LOGIN → JANGAN TAMPILKAN
        if (targetUserId != currentUserId) return

        val title = message.notification?.title ?: "Notifikasi"
        val body = message.notification?.body ?: "Kamu mendapat pesan baru."

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * Ambil ID user yang sedang login dari DataStore
     */
    private fun getCurrentUserId(): String? = runBlocking {
        val userPref = UserPreference.getInstance(applicationContext.dataStore)
        val user = userPref.getUser().first()

        // Jika belum login → idUser = -1
        if (user.idUser == -1) null else user.idUser.toString()
    }
}