package com.martorell.albert.basketcoach.ui.chat.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.data.mapper.HttpErrorMapper
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.ui.util.UiText

/**
 * Maps typed [DomainError] into user-facing [UiText] localized representations for the Chat screen.
 *
 * @return Localized [UiText] instance.
 */
fun DomainError.toChatUiText(): UiText = when (this) {
    is DomainError.Local.NotFound -> UiText.StringResource(R.string.chat_error_empty)
    is DomainError.Network.NoInternet -> UiText.StringResource(R.string.chat_error_no_internet)
    is DomainError.Network.Timeout -> UiText.StringResource(R.string.chat_error_timeout)
    is DomainError.Network.Unauthorized -> UiText.StringResource(R.string.chat_error_unauthorized)
    is DomainError.Network.ServerError -> {
        val serverMessage = message?.let { HttpErrorMapper.parse(it) }
        if (!serverMessage.isNullOrBlank()) {
            UiText.DynamicString(serverMessage)
        } else {
            UiText.StringResource(R.string.chat_error_generic)
        }
    }
    is DomainError.Generic -> {
        val genericMessage = message?.let { HttpErrorMapper.parse(it) }
        if (!genericMessage.isNullOrBlank()) {
            UiText.DynamicString(genericMessage)
        } else {
            UiText.StringResource(R.string.chat_error_generic)
        }
    }
    else -> UiText.StringResource(R.string.chat_error_generic)
}
