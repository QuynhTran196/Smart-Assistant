package com.smartassistant.app.ui.components

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartassistant.app.R
import com.smartassistant.app.ui.theme.BarlowFontFamily
import com.smartassistant.app.ui.theme.SmartAssistantTheme

val ChatStatusKey = SemanticsPropertyKey<String>("ChatStatus")
var SemanticsPropertyReceiver.chatStatus by ChatStatusKey

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    isGenerating: Boolean,
    isOnline: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Icon(
                painter = painterResource(id = R.mipmap.ic_smartassistant_foreground),
                contentDescription = "Smart Assistant Icon",
                tint = androidx.compose.ui.graphics.Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                // App Title
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontFamily = BarlowFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics {
                        contentDescription = "chat_top_bar_title"
                    }
                )

                // Status subtitle
                subtitle?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status indicator dot
                        val statusColor = when {
                            isGenerating -> MaterialTheme.colorScheme.tertiary
                            isOnline -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = statusColor,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.semantics {
                                contentDescription = if (isGenerating) {
                                    "ai_typing"
                                } else if (isOnline) {
                                    "ai_online"
                                } else {
                                    "ai_offline"
                                }
                            }
                        )
                    }
                }
            }

            // Typing indicator when generating
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("ai_typing_spinner"),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

