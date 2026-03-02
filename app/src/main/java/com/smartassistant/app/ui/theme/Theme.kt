package com.smartassistant.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Smart Assistant Theme
 *
 * Modern Material 3 theme with:
 * - Deep Tech Teal primary palette
 * - Light and Dark mode support
 * - Custom typography with Barlow font
 * - Consistent shape system
 */

// =============================================================================
// LIGHT COLOR SCHEME
// =============================================================================
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,

    // Secondary colors
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,

    // Tertiary colors
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,

    // Background & Surface
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    // Outline
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,

    // Error colors
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    // Inverse colors
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,

    // Scrim
    scrim = ScrimColor
)

// =============================================================================
// DARK COLOR SCHEME
// =============================================================================
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,

    // Secondary colors
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,

    // Tertiary colors
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,

    // Background & Surface
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    // Outline
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    // Error colors
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    // Inverse colors
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,

    // Scrim
    scrim = ScrimColor
)

// =============================================================================
// EXTENDED COLORS (Custom colors not in Material 3)
// =============================================================================
data class ExtendedColors(
    val userBubble: Color,
    val aiBubble: Color,
    val userBubbleText: Color,
    val aiBubbleText: Color,
    val inputBarBackground: Color,
    val iconTint: Color,
    val dividerColor: Color
)

val LightExtendedColors = ExtendedColors(
    userBubble = PrimaryLight,
    aiBubble = AiBubbleColorLight,
    userBubbleText = Color.White,
    aiBubbleText = OnSurfaceLight,
    inputBarBackground = InputBarBackgroundLight,
    iconTint = IconTintLight,
    dividerColor = DividerColorLight
)

val DarkExtendedColors = ExtendedColors(
    userBubble = PrimaryDark,
    aiBubble = AiBubbleColorDark,
    userBubbleText = OnPrimaryDark,
    aiBubbleText = OnSurfaceDark,
    inputBarBackground = InputBarBackgroundDark,
    iconTint = IconTintDark,
    dividerColor = DividerColorDark
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Smart Assistant Theme
 */
@Composable
fun SmartAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}


/**
 * Extension property to access extended colors from MaterialTheme
 */
object SmartAssistantTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}

