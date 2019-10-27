package com.colourcog.booking.domain.service

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.InThePastException
import com.colourcog.booking.domain.errors.UnavailableFacilityException
import com.colourcog.booking.domain.gateways.BookingGateway
import com.colourcog.booking.domain.gateways.FacilityGateway
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class TestCreateBookingService {
    val facilityGateway = mockk<FacilityGateway>(relaxed = true)
    val bookingGateway = mockk<BookingGateway>(relaxed = true)
    val useCase = CreateBookingService(facilityGateway, bookingGateway)

    @ExperimentalCoroutinesApi
    @Test
    fun `past dates are invalid`() {
        assertThrows<InThePastException> {
            coEvery { facilityGateway.getFacility(any())} returns Facility(UUID.randomUUID(), emptyList())
            val timeFrame = TimeFrame(
                LocalDateTime.of(2018, 1, 1, 0, 0),
                LocalDateTime.of(2019, 10, 1, 0, 0)
            )
            runBlockingTest { useCase.action(UUID.randomUUID(), timeFrame) }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `overlapping booking raises exception`() {
        coEvery { facilityGateway.getFacility(any())} returns Facility(UUID.randomUUID(), emptyList())
        coEvery { bookingGateway.getBookingsForFacility(any()) } returns sequenceOf(
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
            runBlockingTest { useCase.action(UUID.randomUUID(), timeFrame) }
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun `business rules all pass create succeeds`() {
        coEvery { facilityGateway.getFacility(any())} returns Facility(UUID.randomUUID(), emptyList())
        coEvery { bookingGateway.getBookingsForFacility(any()) } returns emptySequence()
        // FIXME: use a timeDelta, you cheapo!
        val timeFrame =TimeFrame(
            LocalDateTime.of(2050,1,1,0,0),
            LocalDateTime.of(2050, 2, 1, 0, 0)
        )
        assertDoesNotThrow {
            runBlockingTest { useCase.action(UUID.randomUUID(), timeFrame) }
        }
    }
}