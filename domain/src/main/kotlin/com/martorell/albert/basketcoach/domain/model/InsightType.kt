package com.martorell.albert.basketcoach.domain.model

enum class InsightType {
    STRENGTH,
    IMPROVEMENT;

    companion object {
        fun fromString(value: String?): InsightType {
            return entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: IMPROVEMENT
        }
    }
}
