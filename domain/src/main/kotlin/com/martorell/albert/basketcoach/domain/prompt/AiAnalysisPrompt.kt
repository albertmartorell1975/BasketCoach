package com.martorell.albert.basketcoach.domain.prompt

/**
 * Parameter object encapsulating prompts and inference parameters for AI tactical analysis.
 *
 * @property systemPrompt System instructions establishing tactical persona, constraints, and JSON schema.
 * @property userPrompt User prompt containing the formatted match summary and analysis directives.
 * @property temperature Sampling temperature regulating generation randomness vs strict determinism.
 */
data class AiAnalysisPrompt(
    val systemPrompt: String,
    val userPrompt: String,
    val temperature: Double = PromptTemplates.ANALYSIS_TEMPERATURE
)
