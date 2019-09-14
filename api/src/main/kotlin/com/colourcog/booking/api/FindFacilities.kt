package com.colourcog.booking.api

interface FindFacilities {
    fun find(request: Request): Response
    data class Request(val tags: List<String>)
    data class Response(val facilities: List<Facility>) {
        data class Facility(val id: String)
    }
}