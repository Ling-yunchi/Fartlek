@file:OptIn(ExperimentalMaterial3Api::class)

package com.lingyunchi.fartlek.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.components.OutlinedNumberField
import com.lingyunchi.fartlek.context.LocalNavController
import com.lingyunchi.fartlek.viewmodels.RunConfig
import com.lingyunchi.fartlek.viewmodels.RunConfigVM

@Composable
fun ConfigEdit(configId: Int) {
    val runConfigVM = viewModel<RunConfigVM>(LocalContext.current as ViewModelStoreOwner)
    val selectedConfigId by runConfigVM.selectedConfigId.collectAsState()
    var configName by remember { mutableStateOf("") }
    var intervals by remember { mutableStateOf(mutableListOf<RunConfig.Interval>()) }
    val newConfigDuration = intervals.sumOf { it.workDuration + it.restDuration }
    val navController = LocalNavController.current
    val context = LocalContext.current

    LaunchedEffect(configId) {
        if (configId >= 0) {
            val runConfig = runConfigVM.runConfigs.value.find { it.id == configId }
            if (runConfig != null) {
                configName = runConfig.name
                intervals = runConfig.intervals.toMutableList() // 重新创建列表以触发重组
            } else {
                throw IllegalArgumentException("configId: $configId not found")
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                title = { Text("Edit Profile", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = configName,
                onValueChange = { configName = it },
                label = { Text("Profile Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newConfigDuration.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Total Duration (Minutes)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Intervals", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    intervals = intervals.toMutableList().apply {
                        add(RunConfig.Interval(0, 0))
                    }
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }

            intervals.forEachIndexed { index, interval ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedNumberField(
                        modifier = Modifier.weight(1f),
                        value = interval.workDuration,
                        onValueChange = {
                            intervals = intervals.toMutableList().apply {
                                set(
                                    index,
                                    interval.copy(workDuration = it)
                                )
                            }
                        },
                        label = { Text("Running Time (Minutes)") },
                        colors = OutlinedTextFieldDefaults.colors(),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedNumberField(
                        modifier = Modifier.weight(1f),
                        value = interval.restDuration,
                        onValueChange = {
                            intervals = intervals.toMutableList().apply {
                                set(
                                    index,
                                    interval.copy(restDuration = it)
                                )
                            }
                        },
                        label = { Text("Walking Time (Minutes)") },
                        colors = OutlinedTextFieldDefaults.colors(),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = {
                        intervals = intervals.toMutableList().apply { removeAt(index) }
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (configName.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please enter a profile name",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        for (interval in intervals) {
                            if (interval.workDuration <= 0 || interval.restDuration <= 0) {
                                Toast.makeText(
                                    context,
                                    "Intervals duration must be greater than 0",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                        }

                        val runConfig = RunConfig(
                            id = if (configId >= 0) configId else -1,
                            name = configName,
                            duration = newConfigDuration,
                            intervals = intervals
                        )
                        if (runConfigVM.updateRunConfig(runConfig)) {
                            navController.popBackStack()
                            Toast.makeText(context, "Config saved", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to save config", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(
                        if (configId >= 0) "Save" else "Create",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (configId >= 0) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            runConfigVM.selectConfig(configId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        enabled = configId != selectedConfigId
                    ) {
                        Text(
                            if (selectedConfigId == configId) "Selected" else "Select",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            runConfigVM.removeRunConfig(configId)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigEditPreview() {
    ConfigEdit(configId = -1)
}