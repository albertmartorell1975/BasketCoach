package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.DrillDto
import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.data.network.dto.InsightMetricDto

import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.HttpException
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
            gameContext: String?
        ): String {
            throw UnsupportedOperationException()
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

    @Test
    fun `getInsights returns mapped domain insights and populates local cache`() = runTest {
        val dto = AiAnalysisResponseDto(
            insights = listOf(
                InsightDto(
                    id = "i1",
                    title = "Turnover Vulnerability",
                    description = "Too many lost balls in transition",
                    category = "OFFENSE",
                    relatedGameRounds = listOf(1),
                    type = "IMPROVEMENT",
                    contextTag = "Transició",
                    metric = InsightMetricDto(
                        primaryLabel = "Pèrdues",
                        primaryValue = "42%",
                        primarySubValue = "AST/TO 0.51",
                        secondaryLabel = "Impacte",
                        secondaryValue = "6.4 pts",
                        secondaryTag = "dataset:to",
                        sentiment = "NEGATIVE"
                    ),
                    priority = "Alta",
                    footerNote = "Crític",
                    exploreChatPrompt = "Com millorar?"
                )
            ),
            drills = listOf(
                DrillDto(
                    id = "d1",
                    name = "3v2 Transition Drill",
                    objective = "Improve transition defense",
                    instructions = listOf("Start from baseline", "Run 3v2 break"),
                    focusArea = "Transition"
                )
            )
        )
        fakeRemoteDataSource.analysisResponse = dto

        val insightsResult = repository.getInsights(emptyList())

        assertTrue(insightsResult is Result.Success)
        val insights = (insightsResult as Result.Success).data
        assertEquals(1, insights.size)
        assertEquals("Turnover Vulnerability", insights.first().title)
        assertEquals(InsightCategory.OFFENSE, insights.first().category)
        assertEquals(listOf(1), insights.first().relatedGameRounds)
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)
    }

    @Test
    fun `getDrills uses shared cache when getInsights was called first without second remote call`() = runTest {
        val dto = AiAnalysisResponseDto(
            insights = listOf(
                InsightDto(
                    id = "i1",
                    title = "Title",
                    description = "Desc",
                    category = "DEFENSE",
                    relatedGameRounds = listOf(1),
                    type = "IMPROVEMENT",
                    contextTag = "Tag",
                    metric = InsightMetricDto("L", "V", "S", "SL", "SV", "ST", "NEGATIVE"),
                    priority = "Alta",
                    footerNote = "Note",
                    exploreChatPrompt = "Prompt"
                )
            ),
            drills = listOf(
                DrillDto("d1", "Drill 1", "Obj 1", listOf("Step 1"), "Focus")
            )
        )
        fakeRemoteDataSource.analysisResponse = dto

        val insightsResult = repository.getInsights(emptyList())
        assertTrue(insightsResult is Result.Success)
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)

        val drillsResult = repository.getDrills(emptyList())
        assertTrue(drillsResult is Result.Success)
        val drills = (drillsResult as Result.Success).data
        assertEquals(1, drills.size)
        assertEquals("Drill 1", drills.first().name)
        // Verified: only 1 remote call across both insights and drills
        assertEquals(1, fakeRemoteDataSource.analysisCallCount)
    }

    @Test
    fun `getInsights maps remote HttpException to DomainError Network Unauthorized`() = runTest {
        fakeRemoteDataSource.exceptionToThrow = HttpException(401, "Invalid token")

        val result = repository.getInsights(emptyList())

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.Unauthorized, (result as Result.Error).error)
    }
}
