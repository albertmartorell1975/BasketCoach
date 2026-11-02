package com.martorell.albert.basketcoach.ui.analysis.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.ui.util.UiText

/**
 * Maps a typed [DomainError] into user-facing [UiText] suitable for display on the Analysis screen error view.
 *
 * Translates specific network, local, and generic domain errors into localized string resources
 * or dynamic server messages, preventing hardcoded text in ViewModels and Mappers (Constraint #18).
 *
 * @return [UiText] representing the localized error message.
 */
fun DomainError.toAnalysisUiText(): UiText = when (this) {
    is DomainError.Local.NotFound -> UiText.StringResource(R.string.analysis_error_empty)
    is DomainError.Network.NoInternet -> UiText.StringResource(R.string.analysis_error_no_internet)
    is DomainError.Network.Timeout -> UiText.StringResource(R.string.analysis_error_timeout)
    is DomainError.Network.Unauthorized -> UiText.StringResource(R.string.analysis_error_unauthorized)
    is DomainError.Network.ServerError -> {
        val serverMessage = message
        if (!serverMessage.isNullOrBlank()) {
            UiText.DynamicString(serverMessage)
        } else {
            UiText.StringResource(R.string.analysis_error_generic)
        }
    }
    is DomainError.Generic -> {
        val genericMessage = message
        if (!genericMessage.isNullOrBlank()) {
            UiText.DynamicString(genericMessage)
        } else {
            UiText.StringResource(R.string.analysis_error_generic)
        }
    }
    else -> UiText.StringResource(R.string.analysis_error_generic)
}
