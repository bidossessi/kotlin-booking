package com.colourcog.booking.api

interface CreateBooking {
    fun create(request: Request): Response
    data class Request(val facilityId: String, val tags: List<String>)
    data class Response(val id: String)
}