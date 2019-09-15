package com.colourcog.booking.application

import com.colourcog.booking.api.CreateBooking
import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.InvalidTimeFrameException
import com.colourcog.booking.domain.errors.UnbookableFacilityException
import com.colourcog.booking.domain.gateways.BookingGateway
import com.colourcog.booking.domain.gateways.FacilityGateway
import java.util.*

class CreateBookingService(
    private val facilityGateway: FacilityGateway,
    private val bookingGateway: BookingGateway
) : CreateBooking {
    private fun validateTimeFrame(request: CreateBooking.Request): Unit {
        if (request.timeFrame.fromDate >= request.timeFrame.toDate) {
            throw InvalidTimeFrameException(request.timeFrame.toString())
        }
    }

    private fun validateFacility(request: CreateBooking.Request): Unit {
        val matches = bookingGateway.getBookingsForFacility(request.facilityId)
        if (matches.any { it.overlaps(request.timeFrame) }) {
            throw UnbookableFacilityException(request.facilityId)
        }
    }

    override fun create(request: CreateBooking.Request): CreateBooking.Response {
        // FIXME: we don't use the output, we only want the exception. Find another way.
        facilityGateway.getFacility(request.facilityId)
        // TODO: maybe a better way to sequence these ?
        validateTimeFrame(request)
        validateFacility(request)
        val id = bookingGateway.create(request.toDomain())
        return CreateBooking.Response(id)
    }

}

fun CreateBooking.Request.TimeFrame.toDomain(): TimeFrame = TimeFrame(this.fromDate, this.toDate)
fun CreateBooking.Request.toDomain(): Booking =
    Booking(
        UUID.randomUUID().toString(),
        this.facilityId,
        timeFrame.toDomain(),
        tags
    )

fun Booking.overlaps(timeFrame: CreateBooking.Request.TimeFrame): Boolean {
    return this.timeFrame.dateFrom <= timeFrame.toDate && this.timeFrame.dateTo >= timeFrame.fromDate
}