package com.colourcog.booking.domain.entities

import java.util.*

data class Booking (
    val id: UUID,
    val facilityId: UUID,
    val timeFrame: TimeFrame,
    val tags: List<String>
)