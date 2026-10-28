package com.martorell.albert.basketcoach.ui.dashboard.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.ui.util.UiText

/**
 * Maps a typed [DomainError] to a localized [UiText] representation for the Dashboard screen.
 *
 * Keeps ViewModels decoupled from Android string resources (`R.string`) while ensuring
 * user-friendly error messages with fallback support.
 *
 * @receiver The [DomainError] emitted by the data or use case layer.
 * @return A [UiText] encapsulating either a localized string resource ID or dynamic message.
 */
fun DomainError.toDashboardUiText(): UiText = when (this) {
    is DomainError.Local.NotFound -> UiText.StringResource(R.string.dashboard_error_empty)
    is DomainError.Network.ServerError -> {
        val serverMessage = message
        if (!serverMessage.isNullOrBlank()) {
            UiText.DynamicString(serverMessage)
        } else {
            UiText.StringResource(R.string.dashboard_error_generic)
        }
    }
    is DomainError.Generic -> {
        val genericMessage = message
        if (!genericMessage.isNullOrBlank()) {
            UiText.DynamicString(genericMessage)
        } else {
            UiText.StringResource(R.string.dashboard_error_generic)
        }
    }
    else -> UiText.StringResource(R.string.dashboard_error_generic)
}
