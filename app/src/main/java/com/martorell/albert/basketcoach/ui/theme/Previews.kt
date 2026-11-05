package com.martorell.albert.basketcoach.ui.theme

import androidx.compose.ui.tooling.preview.Preview

private const val TABLET_LANDSCAPE_DEVICE = "spec:width=1280dp,height=800dp,dpi=240,orientation=landscape" // Baseline for BasketCoach MVP
private const val LARGE_TABLET_LANDSCAPE_DEVICE = "spec:width=1920dp,height=1200dp,dpi=240,orientation=landscape" // Large Tablet

/**
 * Multi-preview annotation that represents the official Pia Sabadell theme on the target baseline tablet.
 *
 * Configured for the primary BasketCoach surface: Tablet 10" Landscape (1280x800dp).
 */
@Preview(name = "Tablet 10\" Landscape (Baseline)", device = TABLET_LANDSCAPE_DEVICE, showBackground = true)
annotation class BcThemePreview

/**
 * Multi-preview annotation that represents large tablet landscape device scaling.
 *
 * Used alongside [BcThemePreview] to verify layout scaling on high-density displays (1920x1200dp).
 */
@Preview(name = "Tablet 12\" Landscape (Large)", device = LARGE_TABLET_LANDSCAPE_DEVICE, showBackground = true)
annotation class BcDevicePreview
