package com.lingoflow.android.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lingoflow.android.ui.components.TokenTextField
import com.lingoflow.android.ui.theme.Accent

@Composable
fun OnboardingScreen(
    onSetupComplete: () -> Unit,
    onDownloadModel: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val shouldShowOnboarding by viewModel.shouldShowOnboarding.collectAsState()

    LaunchedEffect(shouldShowOnboarding) {
        if (!shouldShowOnboarding) {
            onSetupComplete()
        }
    }

    if (!shouldShowOnboarding) return

    var showCloudSetup by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf("") }
    var isValidating by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Lingoflow",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Practice conversations in any language",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))

        if (!showCloudSetup) {
            Text(
                text = "Choose how you want to practice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.setupOnDevice()
                    onDownloadModel()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Use On-Device Model", modifier = Modifier.padding(vertical = 8.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Works offline. Requires ~4.4 GB download.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showCloudSetup = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Cloud Model (Gemini)", modifier = Modifier.padding(vertical = 8.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Requires internet and a Gemini API key.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val uriHandler = LocalUriHandler.current

            Text(
                text = "Set up Gemini API",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            val linkColor = MaterialTheme.colorScheme.primary
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant
            val annotated = buildAnnotatedString {
                withStyle(SpanStyle(color = textColor)) {
                    append("Get a free API key from ")
                }
                pushStringAnnotation(tag = "URL", annotation = "https://aistudio.google.com/apikey")
                withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
                    append("Google AI Studio")
                }
                pop()
                withStyle(SpanStyle(color = textColor)) {
                    append(", then paste it below.")
                }
            }
            @Suppress("DEPRECATION")
            androidx.compose.foundation.text.ClickableText(
                text = annotated,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                onClick = { offset ->
                    annotated.getStringAnnotations("URL", offset, offset)
                        .firstOrNull()?.let { uriHandler.openUri(it.item) }
                }
            )
            Spacer(Modifier.height(16.dp))

            TokenTextField(
                value = apiKeyInput,
                onValueChange = {
                    apiKeyInput = it
                    validationError = null
                },
                label = "Gemini API Key"
            )

            validationError?.let { error ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    isValidating = true
                    validationError = null
                    viewModel.validateAndSetupCloud(apiKeyInput) { success, error ->
                        isValidating = false
                        if (success) {
                            onSetupComplete()
                        } else {
                            validationError = error
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKeyInput.isNotBlank() && !isValidating,
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                if (isValidating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Continue", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showCloudSetup = false },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isValidating
            ) {
                Text("Back")
            }
        }
    }
}
