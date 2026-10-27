package com.martorell.albert.basketcoach.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

private const val THEME_GROUP = "Themes"
private const val LAYOUT_GROUP = "Layout"
private const val ACCESSIBILITY_GROUP = "Accessibility"
private const val RTL_LOCALE = "ar"
private const val MAX_FONT_SCALE = 2.0f

private const val TABLET_DEVICE = "spec:width=1280dp,height=800dp,dpi=240"
private const val TABLET_LANDSCAPE_DEVICE = "spec:width=1280dp,height=800dp,dpi=240,orientation=landscape" // Default for BasketCoach
private const val PHONE_DEVICE = "spec:width=411dp,height=891dp"
private const val PHONE_LANDSCAPE_DEVICE = "spec:width=891dp,height=411dp,orientation=landscape"

/**
 * Multi-preview annotation that represents various theme configurations (Light, Dark),
 * Layout direction (RTL), and Accessibility modes (Large Font).
 */
@Preview(name = "Light Mode", group = THEME_GROUP, showBackground = true)
@Preview(name = "Dark Mode", group = THEME_GROUP, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "RTL", group = LAYOUT_GROUP, locale = RTL_LOCALE)
@Preview(name = "Large Font", group = ACCESSIBILITY_GROUP, fontScale = MAX_FONT_SCALE)
annotation class BcThemePreview

/**
 * Multi-preview annotation that represents various device form factors.
 * Since BasketCoach is a Tablet Landscape application, we prioritize tablet layouts.
 */
@Preview(name = "Tablet Landscape", device = TABLET_LANDSCAPE_DEVICE)
@Preview(name = "Phone Landscape", device = PHONE_LANDSCAPE_DEVICE)
annotation class BcDevicePreview
