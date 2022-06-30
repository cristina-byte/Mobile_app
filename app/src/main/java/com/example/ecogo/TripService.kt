package com.example.ecogo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class TripService : Service() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        var time:Long=intent!!.getLongExtra("time",0)
        println("Timp"+time)
        var m=time/60
        var s=time%60
        var notification= NotificationCompat.Builder(this,"ChannelId1")
            .setContentTitle(intent?.getStringExtra("punctRidicare")).setContentText("Au ramas "+m+"m "+s+"s"+" până la sfârșitul călătoriei!").setSmallIcon(R.drawable.icon).
            build()
        startForeground(1,notification)
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        var notification=NotificationChannel("ChannelId1","Foreground notification",NotificationManager.IMPORTANCE_DEFAULT)
        var manager=getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notification)
    }

    override fun onBind(intent: Intent): IBinder { TODO("Return the communication channel to the service.") }
}