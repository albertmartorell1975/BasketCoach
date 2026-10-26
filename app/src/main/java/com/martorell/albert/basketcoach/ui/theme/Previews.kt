package com.martorell.albert.basketcoach.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

private const val TABLET_LANDSCAPE_SPEC = "spec:width=1280dp,height=800dp,dpi=240,orientation=landscape"
private const val THEME_GROUP = "Themes"
private const val ACCESSIBILITY_GROUP = "Accessibility"
private const val MAX_FONT_SCALE = 1.5f

@Preview(
    name = "Tablet Landscape - Light",
    group = THEME_GROUP,
    device = TABLET_LANDSCAPE_SPEC,
    showBackground = true
)
@Preview(
    name = "Tablet Landscape - Dark",
    group = THEME_GROUP,
    device = TABLET_LANDSCAPE_SPEC,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Tablet Landscape - Large Font",
    group = ACCESSIBILITY_GROUP,
    device = TABLET_LANDSCAPE_SPEC,
    fontScale = MAX_FONT_SCALE
)
annotation class TabletLandscapePreview

@Preview(name = "Component - Light", group = THEME_GROUP, showBackground = true)
@Preview(name = "Component - Dark", group = THEME_GROUP, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ComponentPreview
