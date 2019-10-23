package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.errors.NoSuchFacilityException
import com.colourcog.booking.domain.gateways.FacilitiesQuery
import com.colourcog.booking.persistence.sqlite.gateway.FacilityGatewayImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.sql.DriverManager
import java.util.*


class TestFacilityGatewayImpl {

    val connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    val gateway = FacilityGatewayImpl(connection)

    val facilityUUID = UUID.randomUUID()
    val facilities = listOf<Facility>(
        Facility(UUID.randomUUID(), listOf("a", "b", "c")),
        Facility(UUID.randomUUID(), listOf("a", "d", "c")),
        Facility(facilityUUID, listOf("r", "d", "w")),
        Facility(UUID.randomUUID(), listOf("h", "r", "g")),
        Facility(UUID.randomUUID(), listOf("e", "b", "d"))
    )

    @Test
    fun `assert we can create facilities`() {
        assertDoesNotThrow {
            gateway.create(Facility(UUID.randomUUID(), listOf("fee", "fi", "fo")))
        }
    }

    @Test
    fun `assert we find the correct facilities with a search`() {
        facilities.forEach { gateway.create(it) }
        val query = FacilitiesQuery(listOf("a", "g"))
        val results = gateway.findFacilities(query)
        assertEquals(3, results.count())
    }

    @Test
    fun `a query with no matches returns an empty sequence`() {
        val query = FacilitiesQuery(listOf("q", "m"))
        val results = gateway.findFacilities(query)
        assertEquals(0, results.count())
    }

    @Test
    fun `make sure we get the correct exception when the facility doesn't exist`() {
        assertThrows<NoSuchFacilityException>  {
            gateway.getFacility(UUID.randomUUID())
        }
    }

    @Test
    fun `assert we can get a single facility`() {
        facilities.forEach { gateway.create(it) }
        val f = gateway.getFacility(facilityUUID)
        assertEquals(listOf("r", "d", "w"), f.tags)
    }
}
