package com.martorell.albert.basketcoach.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Sealed interface representing UI text that can be either a compile-time string resource ID
 * or a runtime dynamic string.
 *
 * Allows ViewModels and presentation mappers to model user-facing text without leaking Android
 * [android.content.Context] or `android.R.string` dependencies into non-UI layers.
 */
sealed interface UiText {

    /**
     * Text represented as a raw dynamic [String] (e.g. from network or exception messages).
     *
     * @property value The string literal to display.
     */
    data class DynamicString(val value: String) : UiText

    /**
     * Text represented as an Android string resource ID.
     *
     * @property resId The integer resource identifier from `R.string`.
     */
    data class StringResource(@get:StringRes val resId: Int) : UiText

    /**
     * Resolves the [UiText] into a concrete [String] within a Composable scope.
     *
     * @return The localized string resolved via [stringResource] or the raw dynamic value.
     */
    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is StringResource -> stringResource(resId)
    }
}
