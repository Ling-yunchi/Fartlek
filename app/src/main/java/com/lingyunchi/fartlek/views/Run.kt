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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.Running
import com.lingyunchi.fartlek.context.LocalNavController
import com.lingyunchi.fartlek.ui.theme.FartlekTheme
import com.lingyunchi.fartlek.ui.theme.Gray600
import com.lingyunchi.fartlek.ui.theme.Purple400
import com.lingyunchi.fartlek.ui.theme.Sky400
import com.lingyunchi.fartlek.viewmodels.RunConfigVM

@Composable
fun Run() {
    val runConfigVM = viewModel<RunConfigVM>(LocalContext.current as ViewModelStoreOwner)
    val runConfigs by runConfigVM.runConfigs.collectAsState()
    val selectedConfigId by runConfigVM.selectedConfigId.collectAsState()
    val currentRunConfig = runConfigs.find { it.id == selectedConfigId }
    val navController = LocalNavController.current

    if (currentRunConfig == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                "Please select a running profile",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    // 计算总时长（单位：分钟）
    val totalDuration = currentRunConfig.intervals.sumOf { it.runMinutes + it.walkMinutes }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // 显示配置名称
            Text(
                text = "Run Profile: ${currentRunConfig.name}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 显示总时长
            Text(
                text = "Total Duration: $totalDuration minutes",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            currentRunConfig.intervals.forEachIndexed { index, interval ->
                Text(
                    text = "Phase ${index + 1}: Run for ${interval.runMinutes} min, Walk for ${interval.walkMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 开始按钮
            Button(
                onClick = {
                    navController.navigate(Running)
                },
                modifier = Modifier.fillMaxWidth(0.5f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Start Run")
            }

//            CircularProgressButton(
//                onLongPressComplete = {
//
//                },
//                longPressDuration = 5,
//            ) {
//                Icon(Icons.Filled.Stop, contentDescription = "Stop")
//            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RunPreview() {
    FartlekTheme {
        Run()
    }
}