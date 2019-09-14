package com.colourcog.booking.persistence.sqlite.gateway

import com.colourcog.booking.domain.entity.Booking
import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.entity.TimeFrame
import com.colourcog.booking.domain.errors.NoSuchBookingException
import com.colourcog.booking.domain.gateway.BookingGateway
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Time
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class BookingGatewayImpl(private val connection: Connection): BookingGateway {

    private val bookingsTable = "bookings"

    init {
        val bookingsInit = """
            CREATE TABLE IF NOT EXISTS $bookingsTable 
            (
                id string PRIMARY KEY, 
                facilityId string KEY, 
                fromDate INTEGER,
                toDate INTEGER,
                tags string, 
                createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """.trimIndent()

        val statement = connection.createStatement()
        statement.execute(bookingsInit)
    }
    private fun buildCreateStatement(booking: Booking): String {
        val dtPair = booking.timeFrame.toTimestampPair()
        return """
            INSERT into $bookingsTable (id, facilityId, fromDate, toDate, tags) 
            VALUES (
                '${booking.id}', 
                '${booking.facilityId}',
                ${dtPair.first},
                ${dtPair.second},
                '${booking.tags.joinToString()}'
            )
        """.trimIndent()
    }

    override fun create(booking: Booking): String {
        val createStr = buildCreateStatement(booking)
        val statement = connection.createStatement()
        statement.execute(createStr)
        return booking.id
    }

    private fun buildGetStatement(id: String): String = "SELECT * from $bookingsTable WHERE id = '$id'"

    override fun getBooking(id: String): Booking {
        val getStr = buildGetStatement(id)
        val statement = connection.createStatement()
        val res = statement.executeQuery(getStr)
        if (!res.next()) throw NoSuchBookingException(id)
        return res.toBooking()
    }

    private fun buildFindStatement(id: String): String = "SELECT * from $bookingsTable WHERE facilityId = '$id'"

    override fun getBookingsForFacility(id: String): Sequence<Booking> {
        val findStr = buildFindStatement(id)
        val statement = connection.createStatement()
        print(findStr)
        val res = statement.executeQuery(findStr)
        return sequence {
            while (res.next()) {
                yield(res.toBooking())
            }
        }
    }
}

fun TimeFrame.toTimestampPair(): Pair<Long, Long> = Pair(
    this.dateFrom.toEpochSecond(ZoneOffset.UTC),
    this.dateTo.toEpochSecond(ZoneOffset.UTC)
)
fun Pair<Long, Long>.toTimeFrame(): TimeFrame = TimeFrame(
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this.first), ZoneOffset.UTC),
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this.second), ZoneOffset.UTC)
)
fun ResultSet.toBooking(): Booking {
    val dtPair = Pair<Long, Long>(getLong("fromDate"), getLong("toDate"))
    return Booking(
        getString("id"),
        getString("facilityId"),
        dtPair.toTimeFrame(),
        getString("tags").split(',').map { it.trim() }.toList()
    )
}
