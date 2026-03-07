package com.smartassistant.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartassistant.app.data.AiSource
import com.smartassistant.app.data.ChatLocalStorage
import com.smartassistant.app.data.ChatRepository
import com.smartassistant.app.data.FileAttachment
import com.smartassistant.app.data.Message
import com.smartassistant.app.data.MessageState
import com.smartassistant.app.data.Sender
import com.smartassistant.app.data.Session
import com.smartassistant.app.ui.components.*
import com.smartassistant.app.ui.theme.BarlowFontFamily
import com.smartassistant.app.ui.theme.SmartAssistantTheme
import com.smartassistant.app.ui.theme.LocalThemeState
import com.smartassistant.app.ui.theme.UserBubbleShape
import com.smartassistant.app.ui.theme.AiBubbleShape
import com.smartassistant.app.viewmodel.ChatViewModel
import com.smartassistant.app.viewmodel.HistoryViewModel
import com.smartassistant.app.viewmodel.ViewModelFactory
import java.util.UUID

@Composable
fun ChatScreen(aiSource: AiSource) {
    val context = LocalContext.current
    val localStorage = remember(context) { ChatLocalStorage(context.applicationContext) }
    val repository = remember(aiSource, localStorage) { ChatRepository(aiSource, localStorage) }
    val chatVm: ChatViewModel = viewModel(factory = ViewModelFactory(repository))
    val historyVm: HistoryViewModel = viewModel(factory = ViewModelFactory(repository))

    val uiState by chatVm.uiState.collectAsState()
    val voiceState by chatVm.voiceState.collectAsState()
    val historyState by historyVm.uiState.collectAsState()

    var showHistory by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var isOnline by remember { mutableStateOf(true) }

    // After AI backend initializes, refresh history from getCache() (the real database)
    LaunchedEffect(aiSource) {
        // Poll until AI is ready, then fetch cached conversations
        while (!aiSource.isReady()) {
            kotlinx.coroutines.delay(500L)
        }
        chatVm.refreshHistoryFromCache()
    }

    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val session = remember { derivedStateOf { uiState.currentSession } }
    val messages = remember { derivedStateOf { uiState.currentMessages } }
    val isGenerating = remember { derivedStateOf { uiState.isGenerating } }
    val pendingAttachments = remember { derivedStateOf { uiState.pendingAttachments } }
    val showAddDialog = remember { derivedStateOf { uiState.showAddDialog } }

    val showSuggestions = remember {
        derivedStateOf {
            messages.value.isEmpty() &&
            !isGenerating.value &&
            !voiceState.isListening &&
            session.value?.showWelcome == true
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val name = if (nameIndex >= 0) cursor.getString(nameIndex) else "Unknown file"
                    val size = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L
                    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

                    chatVm.addAttachment(FileAttachment(
                        id = UUID.randomUUID().toString(),
                        uri = uri,
                        name = name,
                        mimeType = mimeType,
                        size = size
                    ))
                }
            }
        }
    }

    val handleUploadFile: () -> Unit = { filePickerLauncher.launch(arrayOf("*/*")) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            chatVm.startVoice()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    val handleStartVoice: () -> Unit = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            chatVm.startVoice()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(Unit) { chatVm.initVoice(context) }

    // Auto-scroll logic
    val isAtBottom = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem == null || layoutInfo.totalItemsCount == 0 ||
                lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
        }
    }

    var shouldAutoScroll by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress to isAtBottom.value }
            .collect { (isScrolling, atBottom) ->
                if (isScrolling && !atBottom) shouldAutoScroll = false
            }
    }

    val messageCount = messages.value.size
    LaunchedEffect(messageCount) {
        if (messageCount > 0) shouldAutoScroll = true
    }

    val lastMessage = messages.value.lastOrNull()
    val lastMessageLength = lastMessage?.text?.length ?: 0
    val isStreamingNow = lastMessage?.isStreaming == true || isGenerating.value

    LaunchedEffect(messageCount, lastMessageLength) {
        if (messages.value.isNotEmpty() && shouldAutoScroll) {
            delay(30)
            try { listState.scrollToItem(messages.value.lastIndex) } catch (_: Exception) {}
        }
    }

    LaunchedEffect(isStreamingNow) {
        if (isStreamingNow && shouldAutoScroll && messages.value.isNotEmpty()) {
            try { listState.scrollToItem(messages.value.lastIndex) } catch (_: Exception) {}
        }
    }

    // IME handling
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val navBarBottom = WindowInsets.navigationBars.getBottom(density)
    val inputBarOffset = if (imeBottom > navBarBottom) imeBottom - navBarBottom else 0
    val isKeyboardOpen = imeBottom > navBarBottom

    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen && messages.value.isNotEmpty() && shouldAutoScroll) {
            delay(150)
            try { listState.scrollToItem(messages.value.lastIndex) } catch (_: Exception) {}
        }
    }

    val inputBarBaseHeight = 80.dp
    val inputBarHeightPx = with(density) { inputBarBaseHeight.toPx() }
    val totalBottomPadding = with(density) {
        (inputBarHeightPx + inputBarOffset + navBarBottom).toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = totalBottomPadding)
        ) {
            ChatTopBar(
                title = session.value?.title ?: "Smart Assistant",
                subtitle = if (isGenerating.value) "AI is typing…" else if (isOnline) "Online" else "Offline",
                isGenerating = isGenerating.value,
                isOnline = isOnline
            )

            ChatToolbar(
                onHistoryClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    showHistory = true
                },
                onSettingsClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    showSettings = true
                }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                reverseLayout = false
            ) {
                if (session.value?.showWelcome == true && !voiceState.isListening) {
                    item { WelcomeMessage() }
                }
                items(messages.value, key = { it.id }, contentType = { "message" }) { msg ->
                    MessageBubbleNew(msg)
                }
                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset { IntOffset(0, -inputBarOffset) }
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 4.dp)
        ) {
            SuggestionChipsContainer(
                visible = showSuggestions.value,
                onSuggestionClick = { chatVm.sendMessageWithAttachments(it) }
            )

            PendingAttachmentsRow(
                attachments = pendingAttachments.value,
                onRemove = { chatVm.removeAttachment(it) }
            )

            ChatInput(
                isGenerating = isGenerating.value,
                voiceState = voiceState,
                voiceText = null,
                onVoiceConsumed = {},
                onSendMessage = { text ->
                    if (pendingAttachments.value.isNotEmpty() || text.isNotBlank()) {
                        chatVm.sendMessageWithAttachments(text)
                    }
                },
                onStop = chatVm::stopGeneration,
                onAddClicked = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    chatVm.showAddDialog()
                },
                onStartVoice = handleStartVoice,
                onStopVoice = chatVm::stopVoice,
                onCancelVoice = chatVm::cancelVoice,
                modifier = Modifier
            )
        }

        if (showAddDialog.value) {
            AddActionDialog(
                onUploadClick = { handleUploadFile() },
                onNewChatClick = { chatVm.createNewChat(); chatVm.hideAddDialog() },
                onDismiss = chatVm::hideAddDialog
            )
        }

        VoiceListeningOverlay(visible = voiceState.isListening)

        AnimatedDrawerOverlay(
            visible = showHistory,
            onDismiss = { showHistory = false },
            slideFromRight = false
        ) {
            HistoryDrawer(
                sessions = historyState.sessions,
                onSessionClick = { sessionId ->
                    chatVm.selectSession(sessionId)
                    showHistory = false
                },
                onDeleteSession = {
                    historyVm.deleteSession(it)
                    chatVm.removeChat(it)
                    chatVm.createNewChat()
                },
                onNewChat = {
                    chatVm.createNewChat()
                    showHistory = false
                }
            )
        }

        AnimatedDrawerOverlay(
            visible = showSettings,
            onDismiss = { showSettings = false },
            slideFromRight = true
        ) {
            SettingsDrawer(isOnline = isOnline, onOnlineChange = { isOnline = it })
        }
    }
}

@Composable
private fun AnimatedDrawerOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    slideFromRight: Boolean,
    content: @Composable () -> Unit
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (visible) 0.5f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "overlay_alpha"
    )

    val slideOffset by animateIntOffsetAsState(
        targetValue = if (visible) IntOffset.Zero else IntOffset(if (slideFromRight) 800 else -800, 0),
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "slide_offset"
    )

    if (visible || overlayAlpha > 0f) {
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.80f)
                    .align(if (slideFromRight) Alignment.CenterEnd else Alignment.CenterStart)
                    .offset { slideOffset }
            ) {
                content()
            }
        }
    }
}

@Composable
fun MessageBubbleNew(message: Message) {
    val isUser = message.sender == Sender.USER
    val extendedColors = SmartAssistantTheme.extendedColors

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = if (isUser) UserBubbleShape else AiBubbleShape,
            color = if (isUser) extendedColors.userBubble else extendedColors.aiBubble,
            tonalElevation = if (isUser) 0.dp else 1.dp,
            shadowElevation = if (isUser) 2.dp else 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                if (message.attachments.isNotEmpty()) {
                    MessageAttachments(attachments = message.attachments)
                }
                when (message.state) {
                    MessageState.TYPING -> AiTypingIndicator()
                    else -> {
                        if (message.text.isNotBlank()) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                ),
                                color = if (isUser) extendedColors.userBubbleText else extendedColors.aiBubbleText
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * History Drawer Component
 * - Displays chat session history
 * - Allows switching between sessions or creating new ones
 * - Includes delete functionality with haptic feedback
 */
@Composable
fun HistoryDrawer(
    sessions: List<Session>,
    onSessionClick: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onNewChat: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    var clickedSessionId by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .navigationBarsPadding()
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header with "History" title and "New Chat" button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORY",
                    fontSize = 18.sp,
                    fontFamily = BarlowFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNewChat()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Divider aligned with main chat screen divider
            Spacer(Modifier.height(8.dp))
            AppHorizontalDivider()

            // Session list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    HistorySessionItem(
                        session = session,
                        isClicked = clickedSessionId == session.id,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            clickedSessionId = session.id
                        },
                        onClickComplete = {
                            onSessionClick(session.id)
                            clickedSessionId = null
                        },
                        onDelete = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDeleteSession(session.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Individual history session item
 * - Shows session title and last message preview
 * - Provides haptic feedback and visual feedback on click
 * - Delays transition to allow tactile feedback perception
 */
@Composable
private fun HistorySessionItem(
    session: Session,
    isClicked: Boolean,
    onClick: () -> Unit,
    onClickComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animation for tactile feedback effect
    val scale by animateFloatAsState(
        targetValue = when {
            isClicked -> 0.97f
            isPressed -> 0.98f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale_animation"
    )

    // Background color animation
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isClicked -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            isPressed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 150),
        label = "bg_color_animation"
    )

    // Delay transition after click to ensure user sees feedback
    LaunchedEffect(isClicked) {
        if (isClicked) {
            delay(180)
            onClickComplete()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Session title
            Text(
                text = session.title,
                fontSize = 15.sp,
                fontFamily = BarlowFontFamily,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Last message preview
            session.lastMessage?.let {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = it,
                    fontSize = 13.sp,
                    fontFamily = BarlowFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Settings Drawer Component
 * - Displays app settings (Online Mode toggle, Theme Mode)
 * - Provides haptic feedback on interactions
 * - Slides from right side
 */
@Composable
fun SettingsDrawer(isOnline: Boolean, onOnlineChange: (Boolean) -> Unit) {
    val hapticFeedback = LocalHapticFeedback.current
    val themeState = LocalThemeState.current

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header with "Settings" title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SETTINGS",
                    fontSize = 18.sp,
                    fontFamily = BarlowFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Divider aligned with main chat screen divider
            Spacer(Modifier.height(8.dp))
            AppHorizontalDivider()

            // Settings content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Dark Mode Setting Item
                SettingsItem(
                    title = "Dark Mode",
                    description = if (themeState.isDarkMode) "Dark theme is enabled" else "Light theme is enabled",
                    trailing = {
                        Switch(
                            checked = themeState.isDarkMode,
                            onCheckedChange = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                themeState.updateDarkMode(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Online Mode Setting Item
                SettingsItem(
                    title = "Online Mode",
                    description = if (isOnline) "Using cloud AI for responses" else "Using on-device AI for responses",
                    trailing = {
                        Switch(
                            checked = isOnline,
                            onCheckedChange = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onOnlineChange(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                )
            }
        }
    }
}

/**
 * Settings item component
 * - Displays setting title, description, and control
 * - Provides consistent layout for settings
 */
@Composable
private fun SettingsItem(
    title: String,
    description: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontFamily = BarlowFontFamily,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                fontFamily = BarlowFontFamily,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing()
    }
}
