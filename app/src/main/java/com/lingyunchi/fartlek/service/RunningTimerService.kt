package com.lingyunchi.fartlek.service

//import android.app.Service
//import android.content.Intent
//import android.media.MediaPlayer
//import android.os.IBinder
//import com.lingyunchi.fartlek.R
//import java.util.Timer
//import java.util.TimerTask
//
//
//class RunningTimerService : Service() {
//
//    private lateinit var mediaPlayer: MediaPlayer
//    private var elapsedTime: Long = 0
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        createNotificationChannel()
//        startForeground(NOTIFICATION_ID, createNotification())
//
//        Timer().scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                elapsedTime += 1 // 每秒增加
//
//                // 在特定时间播放声音
//                if (alertTimes.contains(elapsedTime)) {
//                    playSound()
//                }
//
//                // 更新通知或保存时间
//                // 这里可以更新通知内容
//            }
//        }, 0, 1000) // 每秒执行一次
//
//        return START_STICKY
//    }
//
//    private fun playSound() {
//        requestAudioFocus()
//
//        mediaPlayer = MediaPlayer.create(this, R.raw.test) // 替换为你的音频文件
//        mediaPlayer.setOnCompletionListener {
//            it.release() // 播放完后释放资源
//            abandonAudioFocus() // 释放音频焦点
//        }
//        mediaPlayer.start()
//    }
//
//    private fun requestAudioFocus() {
//        // 实现音频焦点请求逻辑（如上所示）
//    }
//
//    private fun abandonAudioFocus() {
//        // 实现释放音频焦点逻辑（如上所示）
//    }
//
//    private fun createNotificationChannel() {
//        // 实现通知频道创建逻辑（如上所示）
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.release() // 释放媒体播放器资源
//    }
//}