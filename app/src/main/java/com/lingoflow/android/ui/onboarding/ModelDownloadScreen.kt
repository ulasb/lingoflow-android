package com.lingoflow.android.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lingoflow.android.llm.DownloadState
import com.lingoflow.android.ui.components.TokenTextField
import com.lingoflow.android.ui.theme.Accent

@Composable
fun ModelDownloadScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: ModelDownloadViewModel = hiltViewModel()
) {
    val downloadState by viewModel.downloadState.collectAsState()
    var hfToken by remember { mutableStateOf("") }
    var hasStarted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Download On-Device Model",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))

        if (!hasStarted && downloadState is DownloadState.Idle) {
            // Token input phase
            Text(
                text = "The Gemma model is hosted on HuggingFace and requires a free access token to download.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            HuggingFaceSetupSteps()
            Spacer(Modifier.height(24.dp))

            TokenTextField(
                value = hfToken,
                onValueChange = { hfToken = it },
                label = "HuggingFace Token",
                placeholder = "hf_..."
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    hasStarted = true
                    viewModel.startDownload(hfToken)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = hfToken.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Download (~4.4 GB)", modifier = Modifier.padding(vertical = 8.dp))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        } else {
            // Download progress phase
            Text(
                text = "This may take a few minutes",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            when (val state = downloadState) {
                is DownloadState.Idle -> {
                    Text("Preparing download...")
                }
                is DownloadState.Downloading -> {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Accent
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${(state.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                is DownloadState.Completed -> {
                    Text(
                        "Download complete!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text("Get Started", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                is DownloadState.Failed -> {
                    Text(
                        "Download failed: ${state.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))

                    // Show token field again for editing
                    TokenTextField(
                        value = hfToken,
                        onValueChange = { hfToken = it },
                        label = "HuggingFace Token",
                        placeholder = "hf_..."
                    )
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.retryDownload(hfToken) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hfToken.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text("Retry")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back")
                    }
                }
            }

            if (downloadState is DownloadState.Downloading) {
                Spacer(Modifier.height(24.dp))
                OutlinedButton(onClick = onBack) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun HuggingFaceSetupSteps() {
    val uriHandler = LocalUriHandler.current
    val linkColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textStyle = MaterialTheme.typography.bodySmall

    data class Step(val prefix: String, val linkText: String, val url: String, val suffix: String)

    val steps = listOf(
        Step("1. Create a free account at ", "huggingface.co", "https://huggingface.co/join", ""),
        Step("2. Accept the model license at ", "Gemma 3n E4B", "https://huggingface.co/google/gemma-3n-E4B-it-litert-preview", ""),
        Step("3. Generate a token at ", "Settings > Tokens", "https://huggingface.co/settings/tokens", "")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEach { step ->
            val annotated = buildAnnotatedString {
                withStyle(SpanStyle(color = textColor)) {
                    append(step.prefix)
                }
                pushStringAnnotation(tag = "URL", annotation = step.url)
                withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
                    append(step.linkText)
                }
                pop()
                if (step.suffix.isNotEmpty()) {
                    withStyle(SpanStyle(color = textColor)) {
                        append(step.suffix)
                    }
                }
            }
            androidx.compose.foundation.text.ClickableText(
                text = annotated,
                style = textStyle,
                modifier = Modifier.padding(vertical = 4.dp),
                onClick = { offset ->
                    annotated.getStringAnnotations("URL", offset, offset)
                        .firstOrNull()?.let { uriHandler.openUri(it.item) }
                }
            )
        }
    }
}
