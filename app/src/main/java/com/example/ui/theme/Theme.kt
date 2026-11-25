package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val SahabatLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = CleanWhite,
    primaryContainer = SkyBlueSoft,
    onPrimaryContainer = SkyBlueDark,
    secondary = LavenderPrimary,
    onSecondary = CleanWhite,
    secondaryContainer = LavenderSoft,
    onSecondaryContainer = IndigoDeep,
    tertiary = MintPrimary,
    onTertiary = CleanWhite,
    tertiaryContainer = MintSoft,
    onTertiaryContainer = MintPrimary,
    background = SoftBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SkyBlueSoft,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = EmergencyRed,
    onError = CleanWhite,
    errorContainer = EmergencyRedSoft,
    onErrorContainer = EmergencyRed,
)

private val SahabatShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun SahabatAmanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SahabatLightColorScheme,
        typography = Typography,
        shapes = SahabatShapes,
        content = content,
    )
}
