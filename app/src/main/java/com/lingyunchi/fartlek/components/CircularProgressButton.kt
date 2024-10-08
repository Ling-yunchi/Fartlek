package com.lingyunchi.fartlek.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CircularProgressButton(
    modifier: Modifier = Modifier,
    onLongPressComplete: () -> Unit,
    longPressDuration: Int = 5,
    content: @Composable () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var job: Job?

    val durationMillis = longPressDuration * 1000

    Box(
        modifier = modifier
            .size(100.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        progress.snapTo(0f) // 重置进度
                        job = coroutineScope.launch { // 在当前 CoroutineScope 中启动动画
                            progress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = durationMillis,
                                    easing = LinearEasing
                                )
                            )

                            onLongPressComplete()
                        }

                        try {
                            awaitRelease() // 等待用户释放按钮
                        } finally {
                            job?.cancel()
                            progress.snapTo(0f)
                        }
                    },
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // 圆形进度条
        CircularProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.primary,
        )
        // 按钮
        IconButton(
            onClick = {},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .size(60.dp) // 按钮的大小
                .zIndex(1f) // 确保按钮在进度条上方
        ) {
            content()
        }
    }
}


