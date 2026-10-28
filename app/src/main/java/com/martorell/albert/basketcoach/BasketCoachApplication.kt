package com.martorell.albert.basketcoach

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Root [Application] class for BasketCoach, annotated with [@HiltAndroidApp] to trigger
 * Hilt's code generation and establish the root dependency injection container.
 */
@HiltAndroidApp
class BasketCoachApplication : Application()
