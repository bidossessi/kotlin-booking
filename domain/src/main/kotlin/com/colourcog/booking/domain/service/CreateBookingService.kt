package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.api.CreateBooking
import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.InThePastException
import com.colourcog.booking.domain.errors.UnavailableFacilityException
import com.colourcog.booking.domain.gateways.BookingGateway
import com.colourcog.booking.domain.gateways.FacilityGateway
import java.time.LocalDateTime
import java.util.*

class CreateBookingService(
    private val facilityGateway: FacilityGateway,
    private val bookingGateway: BookingGateway
) : CreateBooking {
    override suspend fun action(facilityId: UUID, timeFrame: TimeFrame, tags: List<String>): UUID {
        val facility = facilityGateway.getFacility(facilityId)
        checkDateThreshold(timeFrame)
        checkUnavailable(facility, timeFrame)
        val booking = Booking(UUID.randomUUID(), facility.id, timeFrame, tags)
        bookingGateway.create(booking = booking)
        return booking.id
    }

    private suspend fun checkUnavailable(facility: Facility, timeFrame: TimeFrame) {
        val matches = bookingGateway.getBookingsForFacility(facility.id)
        if (matches.any { it.timeFrame.overlaps(timeFrame) }) {
            throw UnavailableFacilityException(facility.id.toString())
        }
    }

    private fun checkDateThreshold(timeFrame: TimeFrame) {
        val now = LocalDateTime.now()
        if (!timeFrame.above(now)) {
            throw InThePastException(timeFrame.toString())
        }
    }
}

