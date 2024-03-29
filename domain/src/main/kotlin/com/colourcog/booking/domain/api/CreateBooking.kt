package com.colourcog.booking.domain.api

import com.colourcog.booking.domain.entities.TimeFrame
import java.util.*

interface CreateBooking {
    suspend fun action(facilityId: UUID, timeFrame: TimeFrame, tags: List<String> = emptyList<String>()): UUID
}