package com.colourcog.booking.persistence.sqlite.gateway

import com.colourcog.booking.domain.entities.Booking
import com.colourcog.booking.domain.entities.TimeFrame
import com.colourcog.booking.domain.errors.NoSuchBookingException
import com.colourcog.booking.domain.gateways.BookingGateway
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class BookingGatewayImpl(private val connection: Connection): BookingGateway {

    private val bookingsTable = "bookings"
    private val createStmt: PreparedStatement
    private val getStmt: PreparedStatement
    private val findByFacilityIdStmt: PreparedStatement
    private val findBookedStmt: PreparedStatement

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
        createStmt = connection.prepareStatement(
            """
            INSERT into $bookingsTable (id, facilityId, fromDate, toDate, tags)
            VALUES (?, ?, ? , ?, ?)
            """.trimIndent()
        )
        getStmt = connection.prepareStatement("SELECT * from $bookingsTable WHERE id = ?")
        findByFacilityIdStmt = connection.prepareStatement("SELECT * from $bookingsTable WHERE facilityId = ?")
        findBookedStmt = connection.prepareStatement(
            """
            SELECT facilityId FROM $bookingsTable
            WHERE fromDate <= ?
            AND toDate >= ?
            """.trimIndent()
        )
    }

    override suspend fun create(booking: Booking): Boolean {
        val dtPair = booking.timeFrame.toTimestampPair()
        createStmt.setString(1, booking.id.toString())
        createStmt.setString(2, booking.facilityId.toString())
        createStmt.setLong(3, dtPair.first)
        createStmt.setLong(4, dtPair.second)
        createStmt.setString(5, booking.tags.joinToString())
        createStmt.executeUpdate()
        return true
    }

    override suspend fun create(bookings: List<Booking>): Boolean {
        for (booking in bookings) {
            val dtPair = booking.timeFrame.toTimestampPair()
            createStmt.setString(1, booking.id.toString())
            createStmt.setString(2, booking.facilityId.toString())
            createStmt.setLong(3, dtPair.first)
            createStmt.setLong(4, dtPair.second)
            createStmt.setString(5, booking.tags.joinToString())
            createStmt.addBatch()
        }
        createStmt.executeBatch()
        return true
    }

    override suspend fun getBooking(id: UUID): Booking {
        val idString = id.toString()
        getStmt.setString(1, id.toString())
        val res = getStmt.executeQuery()
        if (!res.next()) throw NoSuchBookingException(idString)
        return res.toBooking()
    }

    override suspend fun getBookingsForFacility(id: UUID): Sequence<Booking> {
        findByFacilityIdStmt.setString(1, id.toString())
        val res = findByFacilityIdStmt.executeQuery()
        return sequence {
            while (res.next()) {
                yield(res.toBooking())
            }
        }
    }
    override suspend fun getBookedFacilityIds(timeFrame: TimeFrame): List<UUID> {
        val dtPair = timeFrame.toTimestampPair()
        findBookedStmt.setLong(1, dtPair.first)
        findBookedStmt.setLong(2, dtPair.second)
        val res = getStmt.executeQuery()
        return sequence {
            while (res.next()) {
                yield(UUID.fromString(res.getString("facilityId")))
            }
        }.toList()
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
        UUID.fromString(getString("id")),
        UUID.fromString(getString("facilityId")),
        dtPair.toTimeFrame(),
        getString("tags").split(',').map { it.trim() }.toList()
    )
}
