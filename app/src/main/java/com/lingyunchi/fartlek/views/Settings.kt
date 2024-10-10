package com.lingyunchi.fartlek.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.components.TextDropdown
import com.lingyunchi.fartlek.ui.theme.FartlekTheme
import com.lingyunchi.fartlek.utils.DarkTheme
import com.lingyunchi.fartlek.utils.toDarkTheme
import com.lingyunchi.fartlek.viewmodels.SettingsVM

@Composable
fun Settings() {
    val settingsVM: SettingsVM = viewModel(LocalContext.current as ViewModelStoreOwner)
    val darkMode by settingsVM.darkMode.collectAsState()
    val runVoiceAnnouncement by settingsVM.runVoiceAnnouncement.collectAsState()
    val walkVoiceAnnouncement by settingsVM.walkVoiceAnnouncement.collectAsState()
    val runVoiceOption = listOf(
        "remain 30s" to 30L,
        "remain 1min" to 60L,
        "remain 2min" to 120L,
        "remain 3min" to 180L,
        "remain 4min" to 240L,
    )
    val walkVoiceOption = listOf(
        "remain 10s" to 10L,
        "remain 30s" to 30L,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        TextDropdown(
            "Dark Theme",
            DarkTheme.entries.map { it.toString() },
            darkMode.toString(),
        ) { settingsVM.setDarkMode(it.toDarkTheme()) }

        Text(
            text = "voice announcement",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "run",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )

        runVoiceOption.forEach { (text, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text)
                Spacer(Modifier.weight(1.0f))
                Checkbox(
                    checked = runVoiceAnnouncement.contains(value),
                    onCheckedChange = { isChecked ->
                        val contain = runVoiceAnnouncement.contains(value)
                        val newList = if (contain && !isChecked) {
                            runVoiceAnnouncement.filter { it != value }
                        } else if (!contain && isChecked) {
                            runVoiceAnnouncement + value
                        } else {
                            return@Checkbox
                        }
                        settingsVM.setRunVoiceAnnouncement(newList)
                    }
                )
            }
        }

        Text(
            text = "walk",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )

        walkVoiceOption.forEach { (text, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text)
                Spacer(Modifier.weight(1.0f))
                Checkbox(
                    checked = walkVoiceAnnouncement.contains(value),
                    onCheckedChange = { isChecked ->
                        val contain = walkVoiceAnnouncement.contains(value)
                        val newList = if (contain && !isChecked) {
                            walkVoiceAnnouncement.filter { it != value }
                        } else if (!contain && isChecked) {
                            walkVoiceAnnouncement + value
                        } else {
                            return@Checkbox
                        }
                        settingsVM.setWalkVoiceAnnouncement(newList)
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    FartlekTheme {
        Settings()
    }
}
