package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.NoSuchBookingException
import com.colourcog.booking.persistence.sqlite.gateway.BookingGatewayImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime
import java.util.*


class TestBookingGatewayImpl {
    val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    val gateway = BookingGatewayImpl(connection)
    val bookingUUID = UUID.randomUUID()
    val facilityUUID = UUID.randomUUID()
    val bookings = listOf<Booking>(
        Booking(
            bookingUUID,
            facilityUUID,
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0, 0),
                LocalDateTime.of(2019, 10, 1, 0, 0)
            ),
            listOf("a", "b", "d")
        ),
        Booking(
            UUID.randomUUID(),
            UUID.randomUUID(),
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0, 0),
                LocalDateTime.of(2019, 10, 1, 0, 0)
            ),
            listOf("a", "x", "d")
        ),
        Booking(
            UUID.randomUUID(),
            UUID.randomUUID(),
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0, 0),
                LocalDateTime.of(2019, 10, 1, 0, 0)
            ),
            listOf("r", "d", "w")
        ),
        Booking(
            UUID.randomUUID(),
            facilityUUID,
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0, 0),
                LocalDateTime.of(2019, 10, 1, 0, 0)
            ),
            listOf("a", "b", "d")
        )
    )

    @ExperimentalCoroutinesApi
    @Test
    fun `creating bookings passes`() {
        assertDoesNotThrow {
            runBlockingTest { gateway.create(bookings) }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `make sure we get the correct exception when the booking doesn't exist`() {
        assertThrows<NoSuchBookingException> {
            runBlockingTest { gateway.getBooking(UUID.randomUUID()) }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `assert we can get a single booking`() = runBlockingTest {
        gateway.create(bookings)
        val f = gateway.getBooking(bookingUUID)
        assertEquals(listOf("a", "b", "d"), f.tags)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `assert we can list bookings for a facility`() = runBlockingTest {
        gateway.create(bookings)
        val matches: Sequence<Booking> = gateway.getBookingsForFacility(facilityUUID)
        assertEquals(2, matches.count())
    }

}