package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.NoSuchBookingException
import com.colourcog.booking.persistence.sqlite.gateway.BookingGatewayImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

class TestBookingGatewayImpl {
    val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    val gateway = BookingGatewayImpl(connection)

    @Test
    fun `creating bookings passes`() {
        val bookings = listOf<Booking>(
            Booking(
                "first",
                "someFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "b", "d")
            ),
            Booking(
                "second",
                "otherFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "x", "d")
            ),
            Booking(
                "third",
                "strangeFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("r", "d", "w")
            ),
            Booking(
                "bookingId",
                "someFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "b", "d"))
        )
        bookings.forEach { gateway.create(it) }
    }

    @Test
    fun `make sure we get the correct exception when the booking doesn't exist`() {
        Assertions.assertThrows(NoSuchBookingException::class.java)  {
            gateway.getBooking("unknown")
        }
    }

    @Test
    fun `assert we can get a single booking`() {
        val bookings = listOf<Booking>(
            Booking(
                "first",
                "someFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "b", "d")
            ),
            Booking(
                "second",
                "otherFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "x", "d")
            ),
            Booking(
                "third",
                "strangeFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("r", "d", "w")
            ),
            Booking(
                "bookingId",
                "someFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "b", "d"))
        )
        bookings.forEach { gateway.create(it) }
        val f = gateway.getBooking("second")
        Assertions.assertEquals(listOf("a", "x", "d"), f.tags)
    }

    @Test
    fun `assert we can list bookings for a facility`() {
        val bookings = listOf<Booking>(
            Booking(
                "first",
                "someFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "b", "d")
            ),
            Booking(
                "second",
                "otherFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "x", "d")
            ),
            Booking(
                "third",
                "strangeFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("r", "d", "w")
            ),
            Booking(
                "bookingId",
                "someFacilityId",
                TimeFrame(
                    LocalDateTime.of(2019, 9, 1, 0,0),
                    LocalDateTime.of(2019, 10, 1, 0 ,0)),
                listOf("a", "b", "d"))
        )
        bookings.forEach { gateway.create(it) }
        val matches: Sequence<Booking> = gateway.getBookingsForFacility("someFacilityId")
        Assertions.assertEquals(2, matches.count())
    }

}