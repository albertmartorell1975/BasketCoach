package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.JsonSanitizer
import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.prompt.AiAnalysisPrompt
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Repository implementation managing AI tactical analysis and practice drills.
 *
 * Implements [AiAnalysisRepository] adhering to the Single Source of Truth (SSOT) pattern
 * by consulting the in-memory cache [AiAnalysisLocalDataSource] first. If unpopulated,
 * dispatches the provided [AiAnalysisPrompt] to [AiRemoteDataSource], sanitizes Markdown
 * code fences, deserializes DTOs, maps to domain entities, and caches results locally.
 *
 * All operations are wrapped in [safeCall] for typed [DomainError] translation and [kotlinx.coroutines.CancellationException] safety.
 *
 * @param remoteDataSource Remote data source communicating with the AI inference endpoint.
 * @param localDataSource In-memory local cache storing generated insights and drills.
 * @param json Kotlinx serialization engine injected via Hilt.
 */
class AiAnalysisRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: AiAnalysisLocalDataSource,
    private val json: Json
) : AiAnalysisRepository {

    /**
     * Retrieves tactical insights for the provided [prompt], returning cached data
     * if available or generating new insights via remote AI inference.
     *
     * @param prompt Parameter object containing system prompt, user prompt, and sampling temperature.
     * @return [Result.Success] containing the list of [Insight] items, or [Result.Error] with [DomainError].
     */
    override suspend fun getInsights(prompt: AiAnalysisPrompt): Result<List<Insight>, DomainError> = safeCall {
        localDataSource.getCachedInsights() ?: run {
            fetchAndCacheAnalysis(prompt)
            localDataSource.getCachedInsights().orEmpty()
        }
    }

    /**
     * Retrieves recommended practice drills for the provided [prompt], returning cached data
     * if available or generating new drills via remote AI inference.
     *
     * @param prompt Parameter object containing system prompt, user prompt, and sampling temperature.
     * @return [Result.Success] containing the list of [Drill] items, or [Result.Error] with [DomainError].
     */
    override suspend fun getDrills(prompt: AiAnalysisPrompt): Result<List<Drill>, DomainError> = safeCall {
        localDataSource.getCachedDrills() ?: run {
            fetchAndCacheAnalysis(prompt)
            localDataSource.getCachedDrills().orEmpty()
        }
    }

    private suspend fun fetchAndCacheAnalysis(prompt: AiAnalysisPrompt) {
        val messages = listOf(
            ChatMessageDto(role = ChatRole.SYSTEM, content = prompt.systemPrompt),
            ChatMessageDto(role = ChatRole.USER, content = prompt.userPrompt)
        )

        val rawContent = remoteDataSource.executeChatCompletion(messages, prompt.temperature)
        val sanitizedJson = JsonSanitizer.stripMarkdownCodeFences(rawContent)
        val dto = json.decodeFromString<AiAnalysisResponseDto>(sanitizedJson)

        val insights = dto.insights.map { it.toDomain() }
        val drills = dto.drills.map { it.toDomain() }
        localDataSource.saveAnalysis(insights, drills)
    }
}
