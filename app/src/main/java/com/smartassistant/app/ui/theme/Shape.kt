package com.smartassistant.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Smart Assistant Shape System
 *
 * Defines consistent rounded corner radii for the entire app.
 * Uses asymmetrical corners for message bubbles to indicate sender.
 */

// =============================================================================
// MATERIAL 3 SHAPES
// =============================================================================
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// =============================================================================
// CUSTOM MESSAGE BUBBLE SHAPES
// =============================================================================

/**
 * User message bubble - rounded with flat bottom-right corner
 * Creates visual anchor to the sender side (right)
 */
val UserBubbleShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 20.dp,
    bottomEnd = 4.dp  // Flat corner toward sender
)

/**
 * AI message bubble - rounded with flat bottom-left corner
 * Creates visual anchor to the sender side (left)
 */
val AiBubbleShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 4.dp,  // Flat corner toward sender
    bottomEnd = 20.dp
)

// =============================================================================
// INPUT & CARD SHAPES
// =============================================================================

/**
 * Input field shape - pill-like for modern feel
 */
val InputFieldShape = RoundedCornerShape(28.dp)

/**
 * Card shape - subtle rounding for containers
 */
val CardShape = RoundedCornerShape(16.dp)

/**
 * Chip shape - small rounded for suggestion chips
 */
val ChipShape = RoundedCornerShape(18.dp)

/**
 * Top bar shape - squared top, rounded bottom for depth
 */
val TopBarShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)
