package com.colourcog.booking.domain.gateways

import com.colourcog.booking.domain.entities.Booking

interface BookingGateway {
    fun create(booking: Booking): String
    fun getBooking(id: String): Booking
    fun getBookingsForFacility(id: String): Sequence<Booking>
}