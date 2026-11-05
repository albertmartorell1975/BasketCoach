package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.HttpException
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.DrillDto
import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.data.network.dto.InsightMetricDto
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AiAnalysisRepositoryImplTest {

    private class FakeAiRemoteDataSource(
        var analysisResponse: AiAnalysisResponseDto? = null,
        var exceptionToThrow: Throwable? = null
    ) : AiRemoteDataSource {
        var analysisCallCount: Int = 0

        override suspend fun generateAnalysis(gameContext: String): AiAnalysisResponseDto {
            analysisCallCount++
            exceptionToThrow?.let { throw it }
            return analysisResponse ?: throw IllegalStateException("No fake response configured")
        }

        override suspend fun generateChatResponse(
            conversationHistory: List<ChatMessageDto>,
            newPrompt: String,
            gameContext: String?,
            topicContext: String?
        ): String {
            throw UnsupportedOperationException()
        }

        override suspend fun isPromptBasketballRelevant(
            prompt: String,
            lastAssistantMessage: String?
        ): Boolean {
            return true
        }
    }

    private lateinit var fakeRemoteDataSource: FakeAiRemoteDataSource
    private lateinit var localDataSource: AiAnalysisLocalDataSourceImpl
    private lateinit var repository: AiAnalysisRepositoryImpl

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeAiRemoteDataSource()
        localDataSource = AiAnalysisLocalDataSourceImpl()
        repository = AiAnalysisRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            localDataSource = localDataSource
        )
    }

    private fun sampleResponseDto(): AiAnalysisResponseDto {
        return AiAnalysisResponseDto(
            insights = listOf(
                InsightDto(
                    id = "insight-1",
                    title = "Defensive Rebounding Deficit",
                    description = "Conceding second chance points consistently across 11 losses.",
                    category = "DEFENSE",
                    relatedGameRounds = listOf(1, 2, 4),
                    type = "IMPROVEMENT",
                    contextTag = "Fase Defensiva",
                    metric = InsightMetricDto(
                        primaryLabel = "Rebots Cedits",
                        primaryValue = "13.2",
                        primarySubValue = "per partit",
                        secondaryLabel = "Diferencial",
                        secondaryValue = "-8.4 net",
                        secondaryTag = "dataset:reb",
                        sentiment = "NEGATIVE"
                    ),
                    priority = "Alta",
                    footerNote = "Àrea d'intervenció prioritària.",
                    exploreChatPrompt = "Com millorem el bloqueig de rebot?"
                )
            ),
            drills = listOf(
                DrillDto(
                    id = "drill-1",
                    name = "3v3 Box Out & Run",
                    objective = "Improve defensive rebounding transition.",
                    instructions = listOf("Set up 3 defenders", "Shoot ball", "Box out for 3 seconds"),
                    focusArea = "Rebounding"
                )
            )
        )
    }

    @Test
    fun `getInsights calls remote source on cache miss, caches results, and returns mapped domain models`() = runTest {
        fakeRemoteDataSource.analysisResponse = sampleResponseDto()

        val insightsResult = repository.getInsights(emptyList())

        assertTrue(insightsResult is Result.Success)
        val insights = (insightsResult as Result.Success).data
        assertEquals(1, insights.size)
        assertEquals("insight-1", insights[0].id)
        assertEquals(InsightCategory.DEFENSE, insights[0].category)
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)

        // Calling getDrills should reuse the same cached response without triggering an additional remote call
        val drillsResult = repository.getDrills(emptyList())
        assertTrue(drillsResult is Result.Success)
        val drills = (drillsResult as Result.Success).data
        assertEquals(1, drills.size)
        assertEquals("drill-1", drills[0].id)
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)
    }

    @Test
    fun `getDrills calls remote source when cache is empty, populating both insights and drills`() = runTest {
        fakeRemoteDataSource.analysisResponse = sampleResponseDto()

        val drillsResult = repository.getDrills(emptyList())

        assertTrue(drillsResult is Result.Success)
        assertEquals(1, (drillsResult as Result.Success).data.size)
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)

        val insightsResult = repository.getInsights(emptyList())
        assertTrue(insightsResult is Result.Success)
        assertEquals(1, (insightsResult as Result.Success).data.size)
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)
    }

    @Test
    fun `getInsights maps remote HttpException to domain ServerError`() = runTest {
        fakeRemoteDataSource.exceptionToThrow = HttpException(500, "Internal Server Error")

        val result = repository.getInsights(emptyList())

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.Network.ServerError)
        assertEquals(500, (error as DomainError.Network.ServerError).code)
    }
}
