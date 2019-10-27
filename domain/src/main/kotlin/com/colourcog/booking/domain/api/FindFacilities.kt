package com.colourcog.booking.domain.api

import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.entities.TimeFrame

interface FindFacilities {
    suspend fun action(tags: List<String> = emptyList(), timeFrame: TimeFrame? = null): Sequence<Facility>
}