package com.colourcog.booking.persistence.sqlite

import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.errors.NoSuchFacilityException
import com.colourcog.booking.domain.gateways.FacilitiesQuery
import com.colourcog.booking.persistence.sqlite.gateway.FacilityGatewayImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.DriverManager


class TestFacilityGatewayImpl {

    val connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    val gateway = FacilityGatewayImpl(connection)

    @Test
    fun `assert we can create facilities`() {
        gateway.create(Facility("c", listOf("fee", "fi", "fo")))
    }

    @Test
    fun `assert we find the correct facilities with a search`() {
        val facilities = listOf<Facility>(
            Facility("first", listOf("a", "b", "c")),
            Facility("second", listOf("a", "d", "c")),
            Facility("third", listOf("r", "d", "w")),
            Facility("fourth", listOf("h", "r", "g")),
            Facility("fifth", listOf("e", "b", "d"))
        )
        facilities.forEach { gateway.create(it) }

        val query = FacilitiesQuery(listOf("a", "g"))
        val results = gateway.findFacilities(query)
        val expected = listOf<String>("first", "second", "fourth")
        val ids = results.map { it.id }.toList()
        Assertions.assertEquals(expected, ids)
    }

    @Test
    fun `a query with no matches returns an empty sequence`() {
        val query = FacilitiesQuery(listOf("q", "m"))
        val results = gateway.findFacilities(query)
        Assertions.assertEquals(0, results.count())
    }

    @Test
    fun `make sure we get the correct exception when the facility doesn't exist`() {
        Assertions.assertThrows(NoSuchFacilityException::class.java)  {
            gateway.getFacility("unknown")
        }
    }

    @Test
    fun `assert we can get a single facility`() {
        val facilities = listOf<Facility>(
            Facility("first", listOf("a", "b", "c")),
            Facility("second", listOf("a", "d", "c")),
            Facility("third", listOf("r", "d", "w")),
            Facility("fourth", listOf("h", "r", "g")),
            Facility("fifth", listOf("e", "b", "d"))
        )
        facilities.forEach { gateway.create(it) }
        val f = gateway.getFacility("third")
        Assertions.assertEquals(listOf("r", "d", "w"), f.tags)
    }
}
