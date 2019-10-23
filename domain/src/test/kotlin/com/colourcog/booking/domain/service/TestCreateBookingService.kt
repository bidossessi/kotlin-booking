package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.InThePastException
import com.colourcog.booking.domain.errors.UnavailableFacilityException
import com.colourcog.booking.domain.gateways.BookingGateway
import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class TestCreateBookingService {
    val facilityGateway = mockk<FacilityGateway>(relaxed = true)
    val bookingGateway = mockk<BookingGateway>(relaxed = true)
    val service = CreateBookingService(facilityGateway, bookingGateway)

    @Test
    fun `past dates are invalid`() {
        assertThrows<InThePastException> {
            val timeFrame = TimeFrame(
                LocalDateTime.of(2018, 1, 1, 0, 0),
                LocalDateTime.of(2019, 10, 1, 0, 0)
            )
            service.create(UUID.randomUUID(), timeFrame)
        }
    }

    @Test
    fun `overlapping booking raises exception`() {
        every { bookingGateway.getBookingsForFacility(any()) } returns sequenceOf(
            Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TimeFrame(
                    LocalDateTime.of(2050, 1, 1, 0, 0),
                    LocalDateTime.of(2050, 10, 1, 0, 0)
                ),
                emptyList<String>()
            )
        )
        assertThrows<UnavailableFacilityException> {
            val timeFrame = TimeFrame(
                LocalDateTime.of(2050, 2, 1, 0, 0),
                LocalDateTime.of(2050, 9, 1, 0, 0)
            )
            service.create(UUID.randomUUID(), timeFrame)
        }

    }

    @Test
    fun `business rules all pass create succeeds`() {
        assertDoesNotThrow {
            // FIXME: use a timeDelta, you cheapo!
            service.create(UUID.randomUUID(), TimeFrame(
                LocalDateTime.of(2050,1,1,0,0),
                LocalDateTime.of(2050, 2, 1, 0, 0)
            ))
        }
    }
}