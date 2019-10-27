package com.colourcog.booking.domain.gateways

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import java.util.*

interface BookingGateway {
    suspend fun create(booking: Booking): Boolean
    suspend fun create(bookings: List<Booking>): Boolean
    suspend fun getBooking(id: UUID): Booking
    suspend fun getBookedFacilityIds(timeFrame: TimeFrame): List<UUID>
    suspend fun getBookingsForFacility(id: UUID): Sequence<Booking>
}