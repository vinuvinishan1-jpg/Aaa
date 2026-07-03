package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = ElectricBlue,
  secondary = ElectricCyan,
  tertiary = NeonPurple,
  background = CosmicBlack,
  surface = DarkSurface,
  onPrimary = androidx.compose.ui.graphics.Color.White,
  onSecondary = CosmicBlack,
  onBackground = TextPrimary,
  onSurface = TextSecondary,
  onTertiary = androidx.compose.ui.graphics.Color.White
)

private val LightColorScheme = DarkColorScheme // Keep it dark futuristic always!

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for that cyber vybe
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve our electric blue theme
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
