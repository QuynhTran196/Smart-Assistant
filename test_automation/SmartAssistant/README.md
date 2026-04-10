# Smart Assistant Test Automation

Robot Framework test automation suite for Smart Assistant Android app.

## 📁 Project Structure

```
test_automation/SmartAssistant/
│
├── Config/                      # Configuration files
│   └── config.resource         # Environment settings
│
├── Resources/                   # Reusable resources
│   ├── Common.resource         # Setup, teardown, utilities
│   └── PageObjects/            # Page Object Model
│       ├── ChatPage.resource   # Chat screen locators & keywords
│       ├── HistoryPage.resource# History drawer locators & keywords
│       ├── SettingsPage.resource# Settings drawer locators & keywords
│       └── VoicePage.resource  # Voice input locators & keywords
│
├── Tests/                       # Test suites
│   ├── Smoke/                  # Quick sanity tests
│   │   └── smoke_tests.robot
│   ├── Functional/             # Feature tests
│   │   ├── chat_tests.robot
│   │   ├── history_tests.robot
│   │   ├── settings_tests.robot
│   │   └── voice_tests.robot
│   └── DataDriven/             # Data-driven tests
│       └── chat_datadriven.robot
│
├── Data/                        # Test data
│   └── chat_messages.csv       # Data-driven test data
│
├── .gitignore                   # Git ignore rules
│
└── results/                     # Test results & reports (generated)
```

## 🚀 Running Tests

### Prerequisites
1. Install Python 3.8+
2. Install Robot Framework: `pip install robotframework`
3. Install Appium Library: `pip install robotframework-appiumlibrary`
4. Start Appium Server: `appium`
5. Connect Android device with Smart Assistant installed

### Run All Tests
```bash
cd test_automation/SmartAssistant
robot --outputdir results Tests/
```

### Run Smoke Tests Only
```bash
robot --outputdir results --include smoke Tests/
```

### Run Specific Test Suite
```bash
robot --outputdir results Tests/Functional/chat_tests.robot
```

### Run by Tags
```bash
robot --outputdir results --include regression Tests/
robot --outputdir results --exclude slow Tests/
```

## 🏷️ Test Tags

| Tag | Description |
|-----|-------------|
| `smoke` | Quick sanity tests (~2 min) |
| `functional` | Feature-specific tests |
| `regression` | Full regression suite |
| `chat` | Chat functionality |
| `history` | History drawer |
| `settings` | Settings drawer |
| `voice` | Voice input |
| `datadriven` | Data-driven tests |
| `stress` | Stress/edge case tests |
| `critical` | Must-pass tests |

## 📱 Test Cases Coverage

### Chat Functionality (11 tests)
- ✅ Send simple message and receive AI response
- ✅ Send long message
- ✅ Send multiple messages in sequence
- ✅ Send message with special characters
- ✅ Send message with numbers
- ✅ Stop AI generation
- ✅ Stop generation and send new message
- ✅ Welcome screen disappears after first message
- ✅ Open add dialog
- ✅ Create new chat from dialog
- ✅ Rapid message sending

### History Drawer (9 tests)
- ✅ Open history drawer
- ✅ Close history drawer
- ✅ Create new chat from drawer
- ✅ New chat resets conversation
- ✅ Session appears in history after chat
- ✅ Switch between sessions
- ✅ Delete session from history
- ✅ Delete active session clears chat
- ✅ Multiple sessions in history

### Settings Drawer (9 tests)
- ✅ Open settings drawer
- ✅ Close settings drawer
- ✅ Verify default online mode
- ✅ Toggle to offline mode
- ✅ Toggle back to online mode
- ✅ Mode persists after closing drawer
- ✅ Online status shows in chat
- ✅ Offline status shows in chat
- ✅ Verify settings UI elements

### Voice Input (10 tests)
- ✅ Mic button is visible
- ✅ Start voice recording shows overlay
- ✅ Stop voice recording hides overlay
- ✅ Cancel voice recording
- ✅ Complete voice input flow
- ✅ Voice button changes during input
- ✅ Voice overlay over welcome screen
- ✅ Voice after text chat
- ✅ Rapid mic button taps
- ✅ Voice during AI generation

### Data-Driven Chat (11 scenarios)
- ✅ Multiple chat scenarios from CSV test data

## 🔧 Configuration

Edit `Config/config.resource` to change:
- Device name
- Appium server URL
- Timeouts
- App package/activity

## 📝 Adding New Tests

1. Add locators to appropriate PageObject file
2. Create keywords in PageObject file
3. Write test cases in Tests/ folder
4. Tag tests appropriately
