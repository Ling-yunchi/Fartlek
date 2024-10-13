package com.lingyunchi.fartlek.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.RunFinished
import com.lingyunchi.fartlek.components.AlertDialogConform
import com.lingyunchi.fartlek.context.LocalNavController
import com.lingyunchi.fartlek.service.RunningService
import com.lingyunchi.fartlek.ui.theme.Gray600
import com.lingyunchi.fartlek.ui.theme.Purple400
import com.lingyunchi.fartlek.ui.theme.Sky400
import com.lingyunchi.fartlek.utils.ListenHandler
import com.lingyunchi.fartlek.utils.milliToMinuteSecond
import com.lingyunchi.fartlek.utils.secondToMinuteSecond
import com.lingyunchi.fartlek.viewmodels.RunConfigVM
import com.lingyunchi.fartlek.viewmodels.RunningVM
import com.lingyunchi.fartlek.viewmodels.SettingsVM
import kotlinx.coroutines.delay


@SuppressLint("InlinedApi")
@Composable
fun Running() {
    val context = LocalContext.current
    val runConfigVM = viewModel<RunConfigVM>(context as ViewModelStoreOwner)
    val runConfigs by runConfigVM.runConfigs.collectAsState()
    val selectedConfigId by runConfigVM.selectedConfigId.collectAsState()
    val currentRunConfig = runConfigs.find { it.id == selectedConfigId }
    val navController = LocalNavController.current

    if (currentRunConfig == null) {
        navController.popBackStack()
        return
    }

    val runningVM = viewModel<RunningVM>()
    val settingsVM = viewModel<SettingsVM>(context as ViewModelStoreOwner)
    val runVoiceAnnouncement by settingsVM.runVoiceAnnouncement.collectAsState()
    val walkVoiceAnnouncement by settingsVM.walkVoiceAnnouncement.collectAsState()

    val totalDuration by runningVM.totalDuration.collectAsState()
    val elapsedTime by runningVM.elapsedTime.collectAsState()
    val currentPhaseIndex by runningVM.currentPhaseIndex.collectAsState()
    val currentPhaseDurationRemaining by runningVM.currentPhaseDurationRemaining.collectAsState()
    val currentIntervalIndex by remember { derivedStateOf { currentPhaseIndex / 2 } }
    val isRunning by runningVM.isRunning.collectAsState()
    val progress by remember { derivedStateOf { elapsedTime / totalDuration.toFloat() } }

    var countdown by remember { mutableStateOf(3) }

    var stopDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        runningVM.setRunConfig(currentRunConfig)
    }

    DisposableEffect(Unit) {
        val serviceIntent = Intent(context, RunningService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        val timerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val timePass =
                    intent?.getLongExtra(RunningService.TIME_UPDATE_EXTRA, 0L) ?: 0L
                runningVM.tick(timePass)
            }
        }
        val intentFilter = IntentFilter(RunningService.TIME_UPDATE)
        context.registerReceiver(timerReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

        val serviceConnection = object : ServiceConnection {
            var notificationHandler: ListenHandler? = null
            var currentPhaseDurationRemainingChangeHandler: ListenHandler? = null
            var phaseChangeHandler: ListenHandler? = null
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val runningService = (service as RunningService.LocalBinder).getService()
                notificationHandler = runningVM.onNotification.register {
                    runningService.updateNotification(it)
                }
                currentPhaseDurationRemainingChangeHandler =
                    runningVM.onCurrentPhaseDurationRemainingChange.register { (idx, lastTime, currentTime) ->
                        val intervalIndex = idx / 2
                        val lastSecond = lastTime / 1000
                        val currentSecond = currentTime / 1000
                        if (idx % 2 == 0) {
                            // run
                            val announcement =
                                runVoiceAnnouncement.firstOrNull { lastSecond > it }
                            if (announcement != null && announcement >= currentSecond) {
                                runningService.speak("第${intervalIndex + 1}节跑步还剩${announcement.secondToMinuteSecond()}")
                            }
                        } else {
                            val announcement = walkVoiceAnnouncement.firstOrNull { lastSecond > it }
                            if (announcement != null && announcement >= currentSecond) {
                                runningService.speak("第${intervalIndex + 1}节步行还剩${announcement.secondToMinuteSecond()}")
                            }
                        }
                    }
                phaseChangeHandler =
                    runningVM.onPhaseChange.register { (currentPhaseIndex, currentPhaseDuration) ->
                        runningService.speak("第${currentPhaseIndex / 2 + 1}节${if (currentPhaseIndex % 2 == 0) "跑步" else "步行"}开始，当前阶段持续时间为${currentPhaseDuration.milliToMinuteSecond()}")
                    }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                notificationHandler?.unRegister()
                notificationHandler = null
                currentPhaseDurationRemainingChangeHandler?.unRegister()
                currentPhaseDurationRemainingChangeHandler = null
            }
        }
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        val stopHandler = runningVM.onStop.register {
            navController.navigate(
                RunFinished(
                    runningVM.startTime.value, elapsedTime, selectedConfigId
                )
            )
        }

        onDispose {
            stopHandler.unRegister()
            context.unbindService(serviceConnection)
            context.stopService(serviceIntent)
            context.unregisterReceiver(timerReceiver)
        }
    }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L) // 每秒更新
            countdown -= 1
        } else {
            runningVM.start()
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // 倒计时界面
            if (countdown > 0) {
                Text(
                    text = "$countdown", style = MaterialTheme.typography.titleLarge
                )
            } else {
                // 正式计时界面
                Text(
                    text = if (currentPhaseIndex % 2 == 0) "Run Phase ${currentIntervalIndex + 1}"
                    else "Walk Phase ${currentIntervalIndex + 1}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Time Left: ${currentPhaseDurationRemaining / 1000}s",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .border(
                            2.dp, Gray600, RoundedCornerShape(24.dp)
                        )
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    currentRunConfig.intervals.forEach { interval ->
                        val workRatio = interval.runMinutes / totalDuration.toFloat()
                        val restRatio = interval.walkMinutes / totalDuration.toFloat()

                        // 显示跑步阶段
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(workRatio)
                                .background(Purple400) // 跑步阶段颜色
                        )

                        // 显示走路阶段
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(restRatio)
                                .background(Sky400) // 走路阶段颜色
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total Progress: ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row {
                    IconButton(
                        onClick = {
                            runningVM.pause(!isRunning)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Pause",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {
                            stopDialogOpen = true
                        }, colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Filled.Stop, contentDescription = "Stop",
                        )
                    }
                }


                // 长按停止按钮
//                CircularProgressButton(
//                    onLongPressComplete = {
//                        navController.popBackStack()
//                    },
//                    longPressDuration = 5,
//                ) {
//                    Icon(Icons.Filled.Stop, contentDescription = "Stop")
//                }
            }
        }
    }
    if (stopDialogOpen) {
        AlertDialogConform(
            onDismissRequest = {
                stopDialogOpen = false
            },
            onConfirmation = {
                runningVM.stop()
                stopDialogOpen = false
            },
            dialogTitle = "Stop",
            dialogText = "Are you sure you want to stop?",
            icon = {
                Icon(Icons.Filled.WarningAmber, contentDescription = "Stop")
            }
        )
    }
}