package com.lingyunchi.fartlek.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lingyunchi.fartlek.R
import com.lingyunchi.fartlek.utils.TTS
import java.util.Locale


class RunningService : Service() {
    private val binder = LocalBinder()
    private var lastTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val timePass = currentTime - lastTime
            lastTime = currentTime
            sendBroadcast(Intent(TIME_UPDATE).apply {
                putExtra(TIME_UPDATE_EXTRA, timePass)
            })
            handler.postDelayed(this, 1000) // 每秒更新一次
        }
    }
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var tts: TTS

    companion object {
        const val TIME_UPDATE = "TimeUpdate"
        const val TIME_UPDATE_EXTRA = "time"
        const val NOTIFICATION_CHANNEL_ID = "RunningService"
        const val NOTIFICATION_CHANNEL_NAME = "Running Service"
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): RunningService = this@RunningService
    }

    override fun onCreate() {
        super.onCreate()
        tts = TTS(this, Locale.CHINESE)
        mediaPlayer = MediaPlayer()
        lastTime = System.currentTimeMillis()
        handler.post(updateTimeRunnable)
        startForeground(1, createNotification(""))
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.destroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun createNotification(contentText: String): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Fartlek")
            .setContentText(contentText)
            .setOngoing(true)
            .build()
    }

    fun updateNotification(contentText: String) {
        val notification = createNotification(contentText)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    fun speak(text: String) {
        tts.speak(text)
    }

    fun playRadio(id: Int) {
        Log.i("PlayRadio", "play radio $id")
        val afd: AssetFileDescriptor = this.resources.openRawResourceFd(id)
        mediaPlayer.apply {
            reset()
            setDataSource(afd)
            prepare()
            start()
        }
    }
}