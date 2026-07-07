# MediSage AI

**AI-Powered Medical Assistant for Android**

MediSage AI is a native Android application built with Kotlin and Jetpack Compose that leverages Google's Gemini 2.5 Flash model to provide evidence-informed health information. It offers a polished chat interface with real-time streaming responses, clinical context awareness, and built-in safety guardrails.

> **Disclaimer:** MediSage AI is for informational purposes only. It does **not** provide medical diagnoses, prescribe medications, or replace professional medical advice. Always consult a qualified healthcare provider for medical concerns. In emergencies, call your local emergency services immediately.

---

## Features

- **AI-Powered Chat** — Real-time streaming responses from Gemini 2.5 Flash, configured with medical safety guardrails
- **Markdown Rendering** — Inline formatting with bold, italic, and critical value highlighting (`[[crit]]...[[/crit]]`)
- **Suggestion Chips** — Contextual follow-up questions surfaced beneath AI responses
- **Chat History** — Persistent session management via Room database (sessions + messages, cascade delete)
- **Authentication Screens** — Login and registration UI (placeholder logic — extend with your auth provider)
- **Navigation Drawer** — Searchable history, new chat creation, and settings access
- **Custom Brand Theme** — Clinical navy color palette with light and dark mode support
- **Streaming Indicator** — Visual feedback during AI response generation

---

## Screens

| Screen | Description |
|---|---|
| **Login** | Brand header, email/password fields, navigation to registration |
| **Create Account** | Registration form with inline validation and legal disclaimer |
| **Chat** | Message feed with streaming AI responses, suggestion chips, and input bar |
| **Navigation Drawer** | Chat history list, new chat button, settings and version info |

---

## Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.3.0 |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (ViewModel + StateFlow + Repository) |
| **DI** | Dagger Hilt 2.59.2 |
| **AI** | Google Generative AI SDK 0.9.0 (Gemini 2.5 Flash) |
| **Local Storage** | Room Database 2.8.4 |
| **Navigation** | Compose Navigation (type-safe routes with kotlinx.serialization) |
| **Min SDK** | Android 7.0 (API 24) |
| **Target SDK** | Android 15 (API 36) |

---

## Getting Started

### Prerequisites

- Android Studio (compatible with AGP 9.2.1+)
- JDK 17+
- An Android device or emulator running API 24+
- A [Google Gemini API key](https://aistudio.google.com/)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/MediSageAI.git
   ```

2. **Add your API key**
   Create or edit `local.properties` in the project root:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Build and run**
   - Open the project in Android Studio
   - Sync Gradle
   - Click **Run** or use the command line:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Project Structure

```
app/src/main/java/com/unreal/medisageai/
├── MainActivity.kt           # Single-activity entry point
├── MediSageApplication.kt    # Hilt application class
├── data/
│   ├── ChatMessage.kt        # Domain model
│   ├── ChatResponse.kt       # Response sealed interface
│   ├── MediSageRepository.kt # Repository interface
│   ├── MediSageRepositoryImpl.kt # Gemini streaming implementation
│   └── local/                # Room database, entities, DAO
├── di/
│   ├── NetworkModule.kt      # Gemini model + repository bindings
│   └── DatabaseModule.kt     # Room database + DAO bindings
└── ui/
    ├── theme/                # Color, typography, theme definitions
    ├── navigation/           # Type-safe routes and nav graph
    ├── auth/                 # Login and Create Account screens
    └── chat/                 # Chat screen, ViewModel, history
```

---

## Build Configuration

Key version catalog entries (`gradle/libs.versions.toml`):

| Dependency | Version |
|---|---|
| Android Gradle Plugin | 9.2.1 |
| Kotlin | 2.3.0 |
| Compose BOM | 2026.02.01 |
| Hilt | 2.59.2 |
| Room | 2.8.4 |
| Generative AI SDK | 0.9.0 |
| kotlinx.serialization | 1.8.1 |

---

## Status

**Development / Pre-release** (v1.0)

- [x] Chat UI with live AI streaming
- [x] Room database wired for persistence
- [x] Themed light/dark mode
- [ ] Real authentication backend
- [ ] Chat history drawer connected to Room DB
- [ ] Attachment and voice input
- [ ] Database migrations (currently using `fallbackToDestructiveMigration()`)

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
