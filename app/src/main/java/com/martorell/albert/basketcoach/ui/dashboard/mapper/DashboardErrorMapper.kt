package com.martorell.albert.basketcoach.ui.dashboard.mapper

import androidx.annotation.StringRes
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.DomainError
import javax.inject.Inject

/**
 * Mapper transforming domain errors into localized string resources for the Dashboard.
 */
class DashboardErrorMapper @Inject constructor() {

    @StringRes
    fun toMessageRes(error: DomainError): Int = when (error) {
        is DomainError.Local.NotFound -> R.string.dashboard_error_empty
        else -> R.string.dashboard_error_generic
    }
}
