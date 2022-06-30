package com.example.ecogo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class PenaltyService : Service() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        var notification= NotificationCompat.Builder(this,"ChannelId2")
            .setContentTitle("Penalizare").setContentText("A-ți fost penalizat cu 2 Ron pentru întarziere").setSmallIcon(R.drawable.icon).
            build()
        startForeground(2,notification)
        return START_STICKY
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        var notification=
            NotificationChannel("ChannelId2","Foreground notification", NotificationManager.IMPORTANCE_DEFAULT)
        var manager=getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notification)
    }
    override fun onBind(intent: Intent): IBinder { TODO("Return the communication channel to the service.") }
}