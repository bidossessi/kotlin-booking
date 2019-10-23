package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.NoSuchBookingException
import com.colourcog.booking.persistence.sqlite.gateway.BookingGatewayImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
                LocalDateTime.of(2019, 9, 1, 0,0),
                LocalDateTime.of(2019, 10, 1, 0 ,0)),
            listOf("a", "b", "d")
        ),
        Booking(
            UUID.randomUUID(),
            UUID.randomUUID(),
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0,0),
                LocalDateTime.of(2019, 10, 1, 0 ,0)),
            listOf("a", "x", "d")
        ),
        Booking(
            UUID.randomUUID(),
            UUID.randomUUID(),
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0,0),
                LocalDateTime.of(2019, 10, 1, 0 ,0)),
            listOf("r", "d", "w")
        ),
        Booking(
            UUID.randomUUID(),
            facilityUUID,
            TimeFrame(
                LocalDateTime.of(2019, 9, 1, 0,0),
                LocalDateTime.of(2019, 10, 1, 0 ,0)),
            listOf("a", "b", "d"))
    )

    @Test
    fun `creating bookings passes`() {
        bookings.forEach { gateway.create(it) }
    }

    @Test
    fun `make sure we get the correct exception when the booking doesn't exist`() {
        assertThrows<NoSuchBookingException>  {
            gateway.getBooking(UUID.randomUUID())
        }
    }

    @Test
    fun `assert we can get a single booking`() {
        bookings.forEach { gateway.create(it) }
        val f = gateway.getBooking(bookingUUID)
        assertEquals(listOf("a", "b", "d"), f.tags)
    }

    @Test
    fun `assert we can list bookings for a facility`() {
        bookings.forEach { gateway.create(it) }
        val matches: Sequence<Booking> = gateway.getBookingsForFacility(facilityUUID)
        assertEquals(2, matches.count())
    }

}