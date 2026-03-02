package com.smartassistant.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartassistant.app.R
import com.smartassistant.app.ui.theme.BarlowFontFamily

/**
 * ============================================================================
 * SuggestionChips.kt - Contextual Chat Suggestions
 * ============================================================================
 *
 * Displays tappable suggestion chips as a compact horizontal scrollable row
 * positioned just above the input field. Users can swipe horizontally to see
 * more suggestions. Lightweight quick actions that don't block main content.
 */

/**
 * Data class representing a single suggestion
 * @param text The suggestion text displayed on the chip
 * @param iconRes Optional drawable resource ID to display before text
 */
data class ChatSuggestion(
    val text: String,
    @DrawableRes val iconRes: Int? = null
)

/**
 * Default suggestions shown to new users
 * These are contextual to a general AI assistant's capabilities
 */
val defaultSuggestions = listOf(
    ChatSuggestion("What can you help me with?", R.drawable.ic_light_bulb),
    ChatSuggestion("Help me write code", R.drawable.ic_wrench),
    ChatSuggestion("Explain a concept", R.drawable.ic_light_bulb),
    ChatSuggestion("Brainstorm ideas", R.drawable.ic_light_bulb),
    ChatSuggestion("Summarize text", R.drawable.ic_diagnostic),
    ChatSuggestion("Translate something", R.drawable.ic_wifi)
)

/**
 * Compact horizontal scrollable suggestion chips container
 *
 * Displays as a single row that users can swipe horizontally.
 * Minimal vertical footprint - doesn't block main content.
 *
 * @param visible Whether suggestions should be shown
 * @param onSuggestionClick Callback when a suggestion is tapped - receives the suggestion text
 * @param modifier Optional modifier for the container
 * @param suggestions List of suggestions to display
 */
@Composable
fun SuggestionChipsContainer(
    visible: Boolean,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    suggestions: List<ChatSuggestion> = defaultSuggestions
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
        modifier = modifier
    ) {
        // OUTER CONTAINER: giữ padding cố định cho toàn bộ LazyRow
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            // Padding tổng
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp) // padding không bị mất khi scroll
            ) {
                items(
                    items = suggestions,
                    key = { it.text }
                ) { suggestion ->
                    SuggestionChip(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion.text) },
                        // dùng để tạo khoảng cách giữa các chip
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    }
}


/**
 * Individual suggestion chip component
 * Compact tappable chip with icon + text using theme colors
 *
 * @param suggestion The suggestion data to display
 * @param onClick Callback when chip is tapped
 * @param modifier Modifier for the chip (used for spacing between chips)
 */
@Composable
private fun SuggestionChip(
    suggestion: ChatSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use theme's primary color for consistency across the app
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display drawable icon if provided
            suggestion.iconRes?.let { iconRes ->
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = primaryColor
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            // Display suggestion text
            Text(
                text = suggestion.text,
                color = primaryColor,
                fontSize = 13.sp,
                fontFamily = BarlowFontFamily,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
