package com.colourcog.booking.domain.entity

data class Booking (
    val id: String,
    val facilityId: String,
    val timeFrame: TimeFrame,
    val tags: List<String>
)