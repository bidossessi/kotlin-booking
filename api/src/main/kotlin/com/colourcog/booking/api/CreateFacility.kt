package com.colourcog.booking.api

interface CreateFacility  {
    fun create(request: Request): Response
    data class Request(val tags: List<String>)
    data class Response(val id: String)
}