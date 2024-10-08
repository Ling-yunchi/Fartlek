package com.lingyunchi.fartlek.views

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.lingyunchi.fartlek.context.LocalNavController
import com.lingyunchi.fartlek.ui.theme.Gray600
import com.lingyunchi.fartlek.ui.theme.Purple400
import com.lingyunchi.fartlek.ui.theme.Sky400
import com.lingyunchi.fartlek.viewmodels.RunConfigVM
import kotlinx.coroutines.delay
import com.lingyunchi.fartlek.RunFinished
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun Running() {
    val runConfigVM = viewModel<RunConfigVM>(LocalContext.current as ViewModelStoreOwner)
    val runConfigs by runConfigVM.runConfigs.collectAsState()
    val selectedConfigId by runConfigVM.selectedConfigId.collectAsState()
    val currentRunConfig = runConfigs.find { it.id == selectedConfigId }
    val navController = LocalNavController.current

    if (currentRunConfig == null) {
        navController.popBackStack()
        return
    }

    val totalDuration = currentRunConfig.intervals.sumOf { it.workDuration + it.restDuration } * 60

    var currentPhaseIndex by remember { mutableStateOf(0) }
    var currentPhaseDuration by remember { mutableStateOf(0) }
    val currentIntervalIndex by remember { derivedStateOf { currentPhaseIndex / 2 } }
    var elapsedTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) } // 5秒倒计时
    var startTime by remember { mutableStateOf(0L) }

    LaunchedEffect(currentRunConfig) {
        currentPhaseIndex = 0
        elapsedTime = 0
        countdown = 5 // 重置倒计时
        val firstInterval = currentRunConfig.intervals.first()
        currentPhaseDuration = firstInterval.workDuration * 60
    }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L) // 每秒更新
            countdown -= 1
        } else {
            isRunning = true // 倒计时结束后开始计时
            startTime = System.currentTimeMillis()
        }
    }

    if (isRunning) {
        LaunchedEffect(elapsedTime) {
            while (elapsedTime < totalDuration) {
                delay(1000L) // 每秒更新
                elapsedTime += 1
                currentPhaseDuration -= 1

                if (currentPhaseDuration <= 0) {
                    // 切换到下一个阶段
                    currentPhaseIndex += 1

                    if (currentIntervalIndex < currentRunConfig.intervals.size) {
                        val interval = currentRunConfig.intervals[currentIntervalIndex]
                        currentPhaseDuration =
                            (if (currentPhaseIndex % 2 == 0) interval.workDuration else interval.restDuration) * 60
                    } else {
                        break
                    }
                }
            }
            navController.navigate(RunFinished(startTime, elapsedTime * 1000, selectedConfigId))
        }
    }

    val progress by remember { derivedStateOf { elapsedTime / totalDuration.toFloat() } }

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
                    text = "Time Left: ${currentPhaseDuration}s",
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
                        val workRatio = interval.workDuration / totalDuration.toFloat()
                        val restRatio = interval.restDuration / totalDuration.toFloat()

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
                        onClick = { isRunning = !isRunning },
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
                            navController.navigate(
                                RunFinished(
                                    startTime,
                                    elapsedTime * 1000,
                                    selectedConfigId
                                )
                            )
                        },
                        colors = IconButtonDefaults.iconButtonColors(
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
}