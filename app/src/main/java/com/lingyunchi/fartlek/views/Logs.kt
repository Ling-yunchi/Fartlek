package com.lingyunchi.fartlek.views

import android.app.AlertDialog
import android.app.Dialog
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.viewmodels.LogsVM
import com.lingyunchi.fartlek.viewmodels.RunConfigVM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Logs() {
    val logsVM = viewModel<LogsVM>()
    val runConfigVM = viewModel<RunConfigVM>()

    val logs by logsVM.logs.collectAsState()
    val runConfigs by runConfigVM.runConfigs.collectAsState()

    if (logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "No Logs",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val sortedLogs by remember {
        derivedStateOf {
            logs.sortedByDescending { it.startTime }.map { log ->
                val configName =
                    runConfigs.find { it.id == log.configId }.let { it?.name ?: "null" }
                val durationText = log.duration.milliseconds.toComponents { minutes, seconds, _ ->
                    "${minutes}m ${seconds}s"
                }
                val timeText = sdf.format(Date(log.startTime))
                return@map object {
                    val id = log.id
                    val configName = configName
                    val durationText = durationText
                    val startTimeText = timeText
                }
            }
        }
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(sortedLogs, key = { it.id }) { log ->
            Surface(onClick = {
                AlertDialog.Builder(context)
                    .setTitle("Delete Log")
                    .setMessage("Are you sure to delete this log?")
                    .setPositiveButton("Yes") { _, _ ->
                        logsVM.removeLog(log.id)
                    }.setNegativeButton("No") { _, _ ->
                    }.show()
            }) {
                ListItem(
                    headlineContent = { Text(log.startTimeText) },
                    supportingContent = { Text(log.durationText) },
                    trailingContent = { Text(log.configName) }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LogsPreview() {
    Logs()
}