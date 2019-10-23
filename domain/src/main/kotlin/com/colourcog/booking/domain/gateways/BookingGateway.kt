package com.colourcog.booking.domain.gateways

import com.colourcog.booking.domain.entities.Booking
import java.util.*

interface BookingGateway {
    fun create(booking: Booking): Unit
    fun getBooking(id: UUID): Booking
    fun getBookingsForFacility(id: UUID): Sequence<Booking>
}