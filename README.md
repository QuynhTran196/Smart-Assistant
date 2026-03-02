# Smart Assistant

A modern Android AI Chat application built with **Jetpack Compose** and **Material 3** following **MVVM architecture**.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Data Flow](#data-flow)
- [Key Components](#key-components)
- [How It Works](#how-it-works)
- [Technologies Used](#technologies-used)

---

## Overview

Smart Assistant is a modern conversational AI application that showcases Android development best practices. The app supports:

- 💬 **Real-time AI Chat** - Stream responses token by token
- 🎤 **Voice Input** - Speak to send messages
- 📜 **Chat History** - View and manage past conversations
- ⚙️ **Settings** - Toggle online/offline mode

---

## Architecture

This project follows the **MVVM (Model-View-ViewModel)** architecture pattern.

```
┌─────────────────────────────────────────────────────────────────┐
│                           USER                                   │
│                             │                                    │
│                        (interaction)                             │
│                             ▼                                    │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                         VIEW                               │  │
│  │                    (ui/ package)                           │  │
│  │                                                            │  │
│  │  ChatScreen.kt ─── UI Components ─── Theme                 │  │
│  │                                                            │  │
│  │  • Displays UI based on state                              │  │
│  │  • Forwards user actions to ViewModel                      │  │
│  │  • NO business logic                                       │  │
│  └───────────────────────────────────────────────────────────┘  │
│                    │                    ▲                        │
│           calls methods         observes StateFlow               │
│                    ▼                    │                        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                      VIEWMODEL                             │  │
│  │                  (viewmodel/ package)                      │  │
│  │                                                            │  │
│  │  ChatViewModel ─── HistoryViewModel ─── ViewModelFactory   │  │
│  │                                                            │  │
│  │  • Holds UI state (StateFlow)                              │  │
│  │  • Processes user actions                                  │  │
│  │  • Contains business logic                                 │  │
│  │  • Survives configuration changes                          │  │
│  └───────────────────────────────────────────────────────────┘  │
│                    │                    ▲                        │
│           calls methods           returns data                   │
│                    ▼                    │                        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                        MODEL                               │  │
│  │                    (data/ package)                         │  │
│  │                                                            │  │
│  │  Repository ─── Data Sources ─── Models                    │  │
│  │                                                            │  │
│  │  • ChatRepository: Single source of truth                  │  │
│  │  • AiSource: AI backend wrapper                            │  │
│  │  • VoiceSource: Speech recognition wrapper                 │  │
│  │  • Models.kt: Data classes                                 │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Why MVVM?

| Principle | Benefit |
|-----------|---------|
| **Separation of Concerns** | Each layer has a single responsibility |
| **Testability** | ViewModels can be unit tested without Android |
| **Maintainability** | Changes in one layer don't affect others |
| **Lifecycle Awareness** | ViewModels survive configuration changes |

---

## Project Structure

```
app/src/main/java/com/smartassistant/app/
│
├── MainActivity.kt              # App entry point
│
├── data/                        # DATA LAYER (Model)
│   ├── AiSource.kt             # AI backend wrapper
│   ├── VoiceSource.kt          # Android SpeechRecognizer wrapper
│   ├── ChatRepository.kt       # Single source of truth for data
│   ├── ChatLocalStorage.kt     # Local persistence
│   ├── Models.kt               # All data classes
│   └── voice/
│       └── VoiceInputState.kt  # Voice state
│
├── viewmodel/                   # VIEWMODEL LAYER
│   └── ViewModels.kt           # ChatViewModel, HistoryViewModel
│
└── ui/                          # VIEW LAYER
    ├── ChatScreen.kt           # Main screen composable
    ├── components/             # Reusable UI components
    │   ├── ChatInput.kt        
    │   ├── ChatTopBar.kt       
    │   ├── MessageBubble.kt    
    │   ├── SuggestionChips.kt  
    │   ├── VoiceRecordButton.kt
    │   └── ...
    └── theme/                  # Material 3 theming
        ├── Color.kt            
        ├── Shape.kt            
        ├── Theme.kt            
        └── Type.kt             
```

---

## Data Flow

### Sending a Message (Unidirectional Data Flow)

```
User taps "Send"
       │
       ▼
┌──────────────────┐
│  ChatScreen.kt   │  VIEW: Calls chatVm.sendMessage(text)
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  ChatViewModel   │  VIEWMODEL: 
│                  │  1. Creates user message
│                  │  2. Creates AI placeholder
│                  │  3. Updates _uiState
│                  │  4. Calls repository.sendMessage()
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  ChatRepository  │  REPOSITORY:
│                  │  1. Saves to session history
│                  │  2. Calls aiSource.sendMessage()
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│    AiSource      │  SOURCE: Sends to AI backend
└────────┬─────────┘
         │
         ▼
    AI Backend
         │
         │ (streaming tokens)
         ▼
┌──────────────────┐
│    AiSource      │  SOURCE: Receives tokens via callback
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  ChatViewModel   │  VIEWMODEL: 
│                  │  1. onToken() callback receives token
│                  │  2. Appends token to message text
│                  │  3. Updates _uiState with new message
└────────┬─────────┘
         │
         │ (StateFlow emission)
         ▼
┌──────────────────┐
│  ChatScreen.kt   │  VIEW: 
│                  │  1. collectAsState() receives new state
│                  │  2. Recomposes UI with updated message
└──────────────────┘
         │
         ▼
    User sees response
```

### State Management

```kotlin
// ViewModel holds state
private val _uiState = MutableStateFlow(ChatUiState())
val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

// View observes state
val uiState by chatVm.uiState.collectAsState()

// ViewModel updates state (immutable)
_uiState.update { currentState ->
    currentState.copy(isGenerating = true)
}
```

---

## Key Components

### 1. Models (`data/Models.kt`)

```kotlin
// Message sender
enum class Sender { USER, AI }

// AI message lifecycle
enum class MessageState { IDLE, TYPING, STREAMING, DONE }

// Single chat message
data class Message(
    val id: String,
    val text: String,
    val sender: Sender,
    val state: MessageState = MessageState.IDLE,
    val isStreaming: Boolean = false
)

// Chat session for history
data class Session(
    val id: String,
    val title: String,
    val lastMessage: String? = null
)

// Complete UI state
data class ChatUiState(
    val sessions: List<ChatSessionState>,
    val currentSessionId: String?,
    val isGenerating: Boolean,
    val showAddDialog: Boolean
)
```

### 2. Repository (`data/ChatRepository.kt`)

```kotlin
class ChatRepository(private val aiSource: AiSource) {
    
    // Observable session list
    val sessions: StateFlow<List<Session>>
    
    // Session management
    fun createSession(): Int
    fun destroySession(sessionId: Int)
    fun deleteSession(sessionId: String)
    
    // Messaging
    fun sendMessage(sessionId: Int, message: String)
    fun registerTokenListener(sessionId: Int, onToken: (String) -> Unit)
    fun stopGeneration(sessionId: Int)
}
```

### 3. ViewModel (`viewmodel/ViewModels.kt`)

```kotlin
class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    
    // UI State
    val uiState: StateFlow<ChatUiState>
    val voiceState: StateFlow<VoiceState>
    
    // Session actions
    fun createNewChat()
    fun selectSession(sessionId: String)
    fun removeChat(sessionId: String)
    
    // Message actions
    fun sendMessage(text: String)
    fun stopGeneration()
    
    // Voice actions
    fun initVoice(context: Context)
    fun startVoice()
    fun stopVoice()
    
    // UI actions
    fun showAddDialog()
    fun hideAddDialog()
}
```

### 4. View (`ui/ChatScreen.kt`)

```kotlin
@Composable
fun ChatScreen(aiSource: AiSource) {
    // Setup
    val repository = remember { ChatRepository(aiSource) }
    val chatVm: ChatViewModel = viewModel(factory = ViewModelFactory(repository))
    
    // Observe state
    val uiState by chatVm.uiState.collectAsState()
    
    // Render UI based on state
    Scaffold(
        bottomBar = { ChatInput(onSendMessage = chatVm::sendMessage) }
    ) {
        LazyColumn {
            items(uiState.currentMessages) { message ->
                MessageBubble(message)
            }
        }
    }
}
```

---

## How It Works

### 1. App Startup

```
MainActivity.onCreate()
       │
       ▼
    App() Composable
       │
       ├── Create AiSource
       │      └── Initialize AI backend
       │      └── Load user profile
       │
       └── ChatScreen(aiSource)
              │
              ├── Create ChatRepository
              ├── Create ChatViewModel
              │      └── Restore cached sessions
              │      └── Create new chat session
              │
              └── Render UI
```

### 2. Voice Input Flow

```
User taps mic button
       │
       ▼
chatVm.startVoice()
       │
       ▼
VoiceSource.start()
       │
       ▼
Android SpeechRecognizer listens
       │
       ▼
User speaks
       │
       ▼
onResult callback with text
       │
       ▼
chatVm.sendMessage(text)
       │
       ▼
(normal message flow)
```

### 3. History Drawer Flow

```
User opens history drawer
       │
       ▼
historyVm.uiState observed
       │
       ▼
HistoryDrawer renders session list
       │
       ▼
User taps a session
       │
       ▼
chatVm.selectSession(id)
       │
       ▼
_uiState.update { copy(currentSessionId = id) }
       │
       ▼
UI recomposes with selected session's messages
```

---

## Technologies Used

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Programming language |
| **Jetpack Compose** | Modern declarative UI toolkit |
| **Coroutines & Flow** | Asynchronous programming |
| **ViewModel** | Lifecycle-aware state holder |
| **StateFlow** | Observable state container |
| **AI Backend SDK** | AI chat capabilities |
| **Android SpeechRecognizer** | Voice input |
| **Material 3** | UI design system |

---

## MVVM Best Practices Applied

1. **Unidirectional Data Flow**
   - Data flows down (ViewModel → View)
   - Events flow up (View → ViewModel)

2. **Single Source of Truth**
   - ChatRepository is the only source for chat data
   - ViewModel holds UI state

3. **Immutable State**
   - All data classes use `val` properties
   - State updates create new instances via `copy()`

4. **Separation of Concerns**
   - View: Only UI rendering
   - ViewModel: Business logic
   - Repository: Data coordination
   - Source: External API access

5. **Dependency Injection Ready**
   - ViewModelFactory pattern used
   - Easy to migrate to Hilt/Koin

---

## Building & Running

```bash
# Clone the repository
git clone <repository-url>

# Open in Android Studio

# Build the project
./gradlew assembleDebug

# Run on device/emulator
./gradlew installDebug
```

---

## License

This project is for portfolio/demonstration purposes.

---

## Author

Personal portfolio project showcasing Android development skills with modern architecture patterns.
