package com.colourcog.booking.domain.entities

import java.util.*

data class Facility(
    val id: UUID,
    val tags: List<String>
)