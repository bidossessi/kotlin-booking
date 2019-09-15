package com.colourcog.booking.api

import java.time.LocalDateTime

interface CreateBooking {
    fun create(request: Request): Response
    data class Request(val facilityId: String, val timeFrame: TimeFrame, val tags: List<String>) {
        data class TimeFrame(val fromDate: LocalDateTime, val toDate: LocalDateTime)
    }
    data class Response(val id: String)
}