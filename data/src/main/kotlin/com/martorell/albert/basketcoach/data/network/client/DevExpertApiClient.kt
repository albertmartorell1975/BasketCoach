package com.martorell.albert.basketcoach.data.network.client

import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Low-level client abstraction for communication with the DevExpert Inference API
 * and OpenAI-compatible endpoints (such as Google Gemini).
 */
interface DevExpertApiClient {
    /**
     * Requests batch tactical analysis and drills based on provided game context.
     *
     * @param gameContext Textual summary of Phase 1 games and metrics.
     * @return [Result.Success] with [AiAnalysisResponseDto] or [Result.Error] with [DomainError].
     */
    suspend fun generateAnalysis(gameContext: String): Result<AiAnalysisResponseDto, DomainError>

    /**
     * Dispatches a single chat completion request with conversation history and game context.
     *
     * @param conversationHistory Prior messages exchanged in the thread.
     * @param newPrompt The new query submitted by the coach.
     * @param gameContext Optional grounding context summarizing Phase 1 games.
     * @return [Result.Success] with the response text or [Result.Error] with [DomainError].
     */
    suspend fun generateChatResponse(
        conversationHistory: List<ChatMessage>,
        newPrompt: String,
        gameContext: String? = null
    ): Result<String, DomainError>
}
