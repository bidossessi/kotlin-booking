package com.colourcog.booking.api

import java.util.*

interface CreateBooking {
    fun create(request: Request): Response
    data class Request(val facilityId: String, val timeFrame: TimeFrame, val tags: List<String>) {
        data class TimeFrame(val fromDate: Date, val toDate: Date)
    }
    data class Response(val id: String)
}