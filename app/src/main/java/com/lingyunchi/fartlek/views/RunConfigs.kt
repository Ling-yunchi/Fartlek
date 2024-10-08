package com.lingyunchi.fartlek.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.ConfigEdie
import com.lingyunchi.fartlek.context.LocalNavController
import com.lingyunchi.fartlek.viewmodels.RunConfigVM


@Composable
fun RunConfig() {
    val runConfigVM = viewModel<RunConfigVM>(LocalContext.current as ViewModelStoreOwner)
    val runConfigs by runConfigVM.runConfigs.collectAsState()
    val selectedConfigId by runConfigVM.selectedConfigId.collectAsState()
    val navController = LocalNavController.current

    if (runConfigs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "No Config Exist",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(runConfigs, key = { it.id }) { config ->
                Surface(onClick = {
                    navController.navigate(ConfigEdie(config.id))
                }) {
                    ListItem(
                        headlineContent = { Text(config.name) },
                        supportingContent = { Text("duration: ${config.duration} min") },
                        trailingContent = {
                            if (selectedConfigId == config.id) Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RunConfigPreview() {
    RunConfig()
}