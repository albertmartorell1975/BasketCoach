package com.martorell.albert.basketcoach.domain.repository

/**
 * Contract providing localized user-facing text resources required by domain and data layers
 * without coupling them to the Android framework.
 */
interface ChatResourceProvider {
    /**
     * Localized rejection message when a user query is determined to be non-basketball / off-topic.
     */
    fun getOffTopicRejectionMessage(): String

    /**
     * Localized label representing the unconstrained open inquiry topic ("Consulta Lliure" / "Consulta Libre").
     */
    fun getOpenInquiryLabel(): String
}
