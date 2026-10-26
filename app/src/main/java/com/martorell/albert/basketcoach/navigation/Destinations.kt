package com.martorell.albert.basketcoach.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {

    @Serializable
    data object Dashboard : Destination

    @Serializable
    data object Chat : Destination
}
