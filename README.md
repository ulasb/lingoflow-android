# Lingoflow Android

A native Android port of [Lingoflow](https://github.com/ulasb/lingoflow), a language learning app where users practice real-world conversations in a target language through scenario-based chat sessions with LLM-powered evaluation.

## What the App Does

Lingoflow helps users learn languages by placing them in practical daily-life scenarios — ordering food at a restaurant, buying a train ticket, checking into a hotel — and having them converse with an AI that plays a native speaker character. The AI stays in character, responds only in the target language, and lets the user work toward a goal (e.g., "successfully order a meal") at their own pace.

After each exchange, the app evaluates whether the user has achieved the scenario goal. When the goal is reached, the app generates a performance summary with specific feedback, increments the user's score, and replaces the completed scenario with a new one. Users can also request hints mid-conversation.

### Key Features

- **Scenario-based practice**: 5 active scenarios at a time, LLM-generated with settings, goals, and descriptions
- **Streaming chat**: Real-time token-by-token response display with typing indicator
- **Goal evaluation**: Automatic detection of goal completion after each turn
- **Performance summaries**: Markdown-formatted feedback on what worked and what to improve
- **Hints**: On-demand suggestions for what to say next, with grammar/vocabulary notes
- **Score tracking**: Running count of completed scenarios
- **Conversation history**: Browse and review past completed conversations with full transcripts
- **Dark/light/system theme**: Material 3 theming with Lingoflow brand colors
- **Multi-language support**: Practice and UI languages are independently configurable

## How It Works

### Two LLM Modes

**On-Device (offline):** Uses [MediaPipe Tasks GenAI](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference) with a Gemma 3 1B int4-quantized model (~529 MB download). The model is downloaded on first launch and loaded eagerly into memory at app startup. Works completely offline after download.

**Cloud (Gemini API):** Calls Google's Gemini API via REST. Supports `gemini-2.0-flash`, `gemini-1.5-flash`, and `gemini-1.5-pro`. API key is stored in Android's `EncryptedSharedPreferences`.

The app presents a first-launch onboarding screen where users choose between the two modes. The choice can be changed later in Settings.

### Architecture

```
UI (Jetpack Compose) → ViewModels → Repositories → Room DB + LlmClient
                                                         ↗         ↘
                                              MediaPipeLlmClient  GeminiApiClient
```

- **UI**: Jetpack Compose with Navigation Compose and Material 3
- **DI**: Hilt for dependency injection
- **Data**: Room database with 4 tables (settings, scenarios, history, messages)
- **LLM**: Abstracted behind an `LlmClient` interface with streaming `Flow<LlmToken>` and blocking variants
- **Prompts**: 5 template files loaded from `assets/prompts/` with `{{variable}}` substitution

### Chat Turn Lifecycle

1. User sends a message → persisted to Room
2. `ChatViewModel` builds the full conversation history and system prompt
3. `LlmClient.generate()` streams tokens → displayed in real-time → bot message persisted
4. `ChatRepository.evaluateGoal()` calls the LLM with the goal evaluation prompt
5. If `REACHED`: mark completed, increment score, generate summary, delete scenario, generate replacement

## Building

### Prerequisites

- Android Studio (with bundled JDK 17+)
- Android SDK Platform 36

### Build

```bash
./gradlew assembleDebug
```

The debug APK is output to `app/build/outputs/apk/debug/app-arm64-v8a-debug.apk`.

### Tests

```bash
./gradlew testDebugUnitTest
```

### Release Build

Configure a signing keystore in `local.properties`, then:

```bash
./gradlew assembleRelease
```

ProGuard rules are pre-configured for MediaPipe, Room, Retrofit, and Gson. ABI splits produce an `arm64-v8a`-only APK.

## Project Structure

```
app/src/main/
├── assets/prompts/              # 5 LLM prompt templates
│   ├── chat_system_prompt.txt
│   ├── conversation_summary.txt
│   ├── generate_scenarios.txt
│   ├── goal_evaluation.txt
│   └── hint_generation.txt
├── java/com/lingoflow/android/
│   ├── LingoflowApp.kt          # @HiltAndroidApp, eager model loading
│   ├── MainActivity.kt          # @AndroidEntryPoint, Compose entry
│   ├── data/
│   │   ├── dao/                  # Room DAOs (Settings, Scenario, History, Message)
│   │   ├── database/             # LingoflowDatabase
│   │   ├── entity/               # Room entities
│   │   └── repository/           # SettingsRepo, ScenarioRepo, HistoryRepo, ChatRepo
│   ├── di/                       # Hilt modules (Database, App, Llm)
│   ├── llm/
│   │   ├── LlmClient.kt         # Interface + LlmToken sealed class
│   │   ├── LlmClientProvider.kt # Routes to correct client by model type
│   │   ├── MediaPipeLlmClient.kt # On-device inference via MediaPipe
│   │   ├── GeminiApiClient.kt   # Cloud inference via Gemini REST API
│   │   ├── ModelDownloadManager.kt # Model download with progress tracking
│   │   └── PromptLoader.kt      # Asset-based template loader
│   └── ui/
│       ├── chat/                 # ChatScreen + ChatViewModel
│       ├── components/           # MarkdownText (Markwon)
│       ├── dashboard/            # DashboardScreen + DashboardViewModel
│       ├── history/              # HistoryScreen, HistoryDetailScreen + ViewModels
│       ├── navigation/           # NavHost, routes, bottom nav
│       ├── onboarding/           # OnboardingScreen, ModelDownloadScreen + ViewModels
│       ├── settings/             # SettingsScreen + SettingsViewModel
│       └── theme/                # Material 3 color scheme + theme
└── res/
    ├── drawable/                 # 6 vector clipart + adaptive icon
    ├── mipmap-anydpi-v26/        # Adaptive launcher icon
    └── values/                   # strings.xml, themes.xml
```

## Porting from the Web App

The original [Lingoflow](https://github.com/ulasb/lingoflow) is a web app built with FastAPI, vanilla JavaScript, SQLite, and Ollama. This Android port preserves all core functionality while adapting to native mobile:

| Web App | Android Port |
|---------|-------------|
| FastAPI backend | Room DB + Repository pattern |
| SQLite via raw SQL | Room with entities, DAOs, and reactive `Flow` |
| Ollama local LLM | MediaPipe Tasks GenAI (Gemma 3 1B) |
| — | Gemini cloud API as alternative |
| Vanilla JS + CSS | Jetpack Compose + Material 3 |
| Server-rendered HTML | MVVM with ViewModels + StateFlow |
| `fetch()` API calls | Direct repository calls within coroutines |
| Jinja-style prompt templates | `assets/prompts/*.txt` with `{{variable}}` substitution |
| JSON file clipart | Vector drawable XML assets |
| Browser dark mode | Material 3 dark/light/system theme |
| `pip install` | Gradle + version catalog |
| Localhost server | Self-contained APK with on-device inference |

All 5 prompt templates (`chat_system_prompt`, `conversation_summary`, `generate_scenarios`, `goal_evaluation`, `hint_generation`) were ported from the original repo with the same template variable scheme.

The database schema mirrors the original's 4 tables (settings, active_scenarios, history, messages) with the same relationships, adapted to Room conventions (auto-generated IDs, foreign key annotations, type-safe DAOs).

The chat turn lifecycle — send message → generate response → evaluate goal → on completion: summarize + increment score + replace scenario — is faithfully reproduced in `ChatRepository` and `ChatViewModel`.

## License

See [LICENSE](LICENSE).
