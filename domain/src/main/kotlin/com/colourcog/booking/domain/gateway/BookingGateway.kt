package com.colourcog.booking.domain.gateway

import com.colourcog.booking.domain.entity.Booking

interface BookingGateway {
    fun create(booking: Booking): String
    fun getBooking(id: String): Booking
    fun getBookingsForFacility(id: String): Sequence<Booking>
}