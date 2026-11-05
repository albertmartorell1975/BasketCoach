package com.martorell.albert.basketcoach.data.resource

import android.content.Context
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.repository.ChatResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete Android framework implementation of [ChatResourceProvider].
 *
 * Resolves user-facing chat strings dynamically from Android resources (`strings.xml`)
 * matching the user's active application locale.
 *
 * @param context Application context used to query localized Android string resources.
 */
@Singleton
class AndroidChatResourceProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ChatResourceProvider {

    override fun getOffTopicRejectionMessage(): String {
        return context.getString(R.string.chat_rejection_off_topic)
    }

    override fun getOpenInquiryLabel(): String {
        return context.getString(R.string.chat_topic_open_badge)
    }
}
