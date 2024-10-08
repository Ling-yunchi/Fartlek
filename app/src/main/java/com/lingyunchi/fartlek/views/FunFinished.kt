package com.lingyunchi.fartlek.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.Main
import com.lingyunchi.fartlek.context.LocalNavController
import com.lingyunchi.fartlek.viewmodels.LogsVM
import com.lingyunchi.fartlek.viewmodels.RunLog
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RunFinished(startTime: Long, duration: Long, configId: Int) {
    val logsVm = viewModel<LogsVM>()
    val navController = LocalNavController.current
    val dDuration = duration.milliseconds

    val message = if (dDuration.inWholeSeconds < 30) {
        "跑步时间不足30秒，请下次努力哦！😭😭😭"
    } else {
        logsVm.addLog(RunLog(logsVm.generateId(), startTime, duration, configId))
        dDuration.toComponents { minutes, second, _ ->
            "跑步完成，时间: ${minutes}分${second}秒！🥰🥰🥰"
        }
    }

    // 添加一些鼓励的华语
    val encouragementMessage = when {
        dDuration.inWholeMinutes <= 15 -> "继续努力，下次一定能更久！"
        dDuration.inWholeMinutes <= 30 -> "很好，继续保持！"
        dDuration.inWholeMinutes <= 45 -> "太棒了，你正在进步！"
        else -> "极限挑战，超越自我！"
    }

    // 显示界面
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 显示跑步图标
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = "Run",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 显示主要消息
        Text(text = message, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // 显示鼓励消息
        Text(text = encouragementMessage, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // 返回按钮
        Button(onClick = { navController.navigate(Main) }) {
            Text(text = "返回")
        }
    }
}