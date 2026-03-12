package com.lingoflow.android.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lingoflow.android.llm.GeminiApiClient
import com.lingoflow.android.ui.components.TokenTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showSaved() {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar("Settings saved", duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Theme
            SectionHeader("Theme")
            Row {
                listOf("light" to "Light", "dark" to "Dark", "system" to "System").forEach { (value, label) ->
                    FilterChip(
                        selected = settings.theme == value,
                        onClick = { viewModel.updateTheme(value); showSaved() },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Practice Language
            SectionHeader("Practice Language")
            LanguageDropdown(
                selected = settings.practiceLanguage,
                onSelect = { viewModel.updatePracticeLanguage(it); showSaved() },
                languages = listOf("Japanese", "Korean", "Chinese", "Spanish", "French", "German", "Italian", "Portuguese")
            )

            Spacer(Modifier.height(16.dp))

            // UI Language
            SectionHeader("UI Language")
            LanguageDropdown(
                selected = settings.uiLanguage,
                onSelect = { viewModel.updateUiLanguage(it); showSaved() },
                languages = listOf("English", "Japanese", "Korean", "Chinese", "Spanish", "French", "German")
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Model Type
            SectionHeader("Model Type")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = settings.modelType == "on_device",
                    onClick = { viewModel.updateModelType("on_device"); showSaved() }
                )
                Text("On-Device", modifier = Modifier.padding(end = 24.dp))
                RadioButton(
                    selected = settings.modelType == "cloud",
                    onClick = { viewModel.updateModelType("cloud"); showSaved() }
                )
                Text("Cloud (Gemini)")
            }

            // Cloud settings
            if (settings.modelType == "cloud") {
                Spacer(Modifier.height(16.dp))

                TokenTextField(
                    value = apiKey,
                    onValueChange = { viewModel.updateApiKey(it); showSaved() },
                    label = "Gemini API Key"
                )

                Spacer(Modifier.height(12.dp))

                SectionHeader("Cloud Model")
                LanguageDropdown(
                    selected = settings.cloudModel,
                    onSelect = { viewModel.updateCloudModel(it); showSaved() },
                    languages = GeminiApiClient.AVAILABLE_MODELS
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown(
    selected: String,
    onSelect: (String) -> Unit,
    languages: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onSelect(language)
                        expanded = false
                    }
                )
            }
        }
    }
}
