package com.smartassistant.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Smart Assistant Color Palette
 *
 * Modern, minimal, tech-oriented design system
 * Primary: Deep Teal/Cyan - Professional, trustworthy, tech-forward
 * Secondary: Warm Coral - Subtle accent for personality
 * Neutral: Cool grays with slight blue undertone
 */

// =============================================================================
// PRIMARY COLORS - Deep Tech Teal
// =============================================================================
val PrimaryLight = Color(0xFF006B5A)          // Deep teal - main actions, user bubbles
val OnPrimaryLight = Color(0xFFFFFFFF)        // White text on primary
val PrimaryContainerLight = Color(0xFF7AF8DC) // Light teal container
val OnPrimaryContainerLight = Color(0xFF00201A) // Dark text on light teal

val PrimaryDark = Color(0xFF5BDBC0)           // Bright teal for dark mode
val OnPrimaryDark = Color(0xFF00382E)         // Dark text on bright teal
val PrimaryContainerDark = Color(0xFF005144)  // Deep teal container
val OnPrimaryContainerDark = Color(0xFF7AF8DC) // Light text on deep teal

// =============================================================================
// SECONDARY COLORS - Warm Coral Accent
// =============================================================================
val SecondaryLight = Color(0xFF4A635D)        // Muted teal-gray
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFCDE8DF) // Light sage
val OnSecondaryContainerLight = Color(0xFF06201B)

val SecondaryDark = Color(0xFFB1CCC4)         // Light sage for dark mode
val OnSecondaryDark = Color(0xFF1C352F)
val SecondaryContainerDark = Color(0xFF334B45)
val OnSecondaryContainerDark = Color(0xFFCDE8DF)

// =============================================================================
// TERTIARY COLORS - Accent Blue
// =============================================================================
val TertiaryLight = Color(0xFF416277)         // Steel blue
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFC4E7FF)
val OnTertiaryContainerLight = Color(0xFF001E2D)

val TertiaryDark = Color(0xFFA8CBE2)          // Soft sky blue
val OnTertiaryDark = Color(0xFF0E3447)
val TertiaryContainerDark = Color(0xFF284B5E)
val OnTertiaryContainerDark = Color(0xFFC4E7FF)

// =============================================================================
// SURFACE & BACKGROUND
// =============================================================================
val BackgroundLight = Color(0xFFF5FBF8)       // Very light mint-white
val OnBackgroundLight = Color(0xFF171D1B)     // Near black
val SurfaceLight = Color(0xFFF5FBF8)
val OnSurfaceLight = Color(0xFF171D1B)

val BackgroundDark = Color(0xFF0F1513)        // Deep charcoal with teal tint
val OnBackgroundDark = Color(0xFFDFE4E0)      // Light gray text
val SurfaceDark = Color(0xFF0F1513)
val OnSurfaceDark = Color(0xFFDFE4E0)

// =============================================================================
// SURFACE VARIANTS - For message bubbles & cards
// =============================================================================
val SurfaceVariantLight = Color(0xFFDAE5E0)   // Light gray-teal for AI bubbles
val OnSurfaceVariantLight = Color(0xFF3F4945) // Medium gray text

val SurfaceVariantDark = Color(0xFF3F4945)    // Dark gray-teal for AI bubbles
val OnSurfaceVariantDark = Color(0xFFBEC9C4)  // Light gray text

// =============================================================================
// OUTLINE & DIVIDER
// =============================================================================
val OutlineLight = Color(0xFF6F7975)
val OutlineVariantLight = Color(0xFFBEC9C4)

val OutlineDark = Color(0xFF89938F)
val OutlineVariantDark = Color(0xFF3F4945)

// =============================================================================
// ERROR COLORS
// =============================================================================
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// =============================================================================
// INVERSE & SCRIM
// =============================================================================
val InverseSurfaceLight = Color(0xFF2B3230)
val InverseOnSurfaceLight = Color(0xFFECF2EE)
val InversePrimaryLight = Color(0xFF5BDBC0)

val InverseSurfaceDark = Color(0xFFDFE4E0)
val InverseOnSurfaceDark = Color(0xFF2B3230)
val InversePrimaryDark = Color(0xFF006B5A)

val ScrimColor = Color(0xFF000000)

// =============================================================================
// ADDITIONAL UI COLORS
// =============================================================================
val UserBubbleColor = PrimaryLight
val AiBubbleColorLight = Color(0xFFE8F5F1)    // Soft mint for AI bubbles (light)
val AiBubbleColorDark = Color(0xFF1E2D28)     // Deep teal-gray for AI bubbles (dark)

val InputBarBackgroundLight = Color(0xFFFFFFFF)
val InputBarBackgroundDark = Color(0xFF1A2420)

val IconTintLight = PrimaryLight
val IconTintDark = PrimaryDark

val DividerColorLight = Color(0xFFE0E6E3)
val DividerColorDark = Color(0xFF2A3530)
