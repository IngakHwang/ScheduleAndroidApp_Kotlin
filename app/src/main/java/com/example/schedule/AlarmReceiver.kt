package com.example.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    val channelName = "channel1"
    val channelID = "channel1"


    override fun onReceive(context: Context, intent: Intent) {
        lateinit var builder : NotificationCompat.Builder

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            )
            builder = NotificationCompat.Builder(context, channelID)
        } else {
            builder = NotificationCompat.Builder(context)
        }

        val title = intent.getStringExtra("Title")
        val memo = intent.getStringExtra("Memo")

        val intent = Intent(context, TodayFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(context,101,intent, PendingIntent.FLAG_MUTABLE)

        builder.setContentTitle(title)
        builder.setContentText(memo)
        builder.setSmallIcon(R.drawable.today)
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)

        manager.notify(1,builder.build())
    }


}