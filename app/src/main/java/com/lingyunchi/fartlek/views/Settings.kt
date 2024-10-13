package com.lingyunchi.fartlek.views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.lingyunchi.fartlek.components.TextDropdown
import com.lingyunchi.fartlek.ui.theme.FartlekTheme
import com.lingyunchi.fartlek.utils.DarkTheme
import com.lingyunchi.fartlek.utils.TTS
import com.lingyunchi.fartlek.utils.toDarkTheme
import com.lingyunchi.fartlek.viewmodels.SettingsVM
import java.util.Locale

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
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TTS?>(null) }

    DisposableEffect(Unit) {
        tts = try {
            TTS(context, Locale.CHINA)
        } catch (e: Exception) {
            null
        }
        onDispose {
            tts?.destroy()
        }
    }

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
                Checkbox(checked = runVoiceAnnouncement.contains(value),
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
                    })
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
                Checkbox(checked = walkVoiceAnnouncement.contains(value),
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
                    })
            }
        }

        var speakText by remember { mutableStateOf("你好，我是逆蝶") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(value = speakText,
                onValueChange = { speakText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Speak Text") })
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (tts == null) {
                    Toast.makeText(context, "Please install the voice engine", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                tts!!.speak(speakText)
            }) {
                Text("Speak")
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
