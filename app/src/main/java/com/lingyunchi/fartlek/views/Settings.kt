@file:OptIn(ExperimentalMaterial3Api::class)

package com.lingyunchi.fartlek.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.lingyunchi.fartlek.utils.DarkTheme
import com.lingyunchi.fartlek.utils.toDarkTheme
import com.lingyunchi.fartlek.ui.theme.FartlekTheme
import com.lingyunchi.fartlek.viewmodels.SettingsVM

@Composable
fun Settings() {
    val settingsVM: SettingsVM = viewModel(LocalContext.current as ViewModelStoreOwner)
    val darkMode by settingsVM.darkMode.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextDropdown(
            "Dark Theme",
            DarkTheme.entries.map { it.toString() },
            darkMode.toString(),
        ) { settingsVM.setDarkMode(it.toDarkTheme()) }
    }
}

@Composable
fun TextDropdown(
    title: String,
    options: List<String>,
    initialValue: String,
    onValueChange: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(initialValue) }
    var expanded by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(32.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            // 显示当前选择
            TextField(
                readOnly = true,
                value = selectedOption,
                onValueChange = { },
                label = null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            // 下拉菜单
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                            onValueChange(selectedOption)
                        }
                    )
                }
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
