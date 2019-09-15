package com.colourcog.booking.persistence.sqlite.gateway

import com.colourcog.booking.domain.entities.Facility
import com.colourcog.booking.domain.errors.NoSuchFacilityException
import com.colourcog.booking.domain.gateways.FacilitiesQuery
import com.colourcog.booking.domain.gateways.FacilityGateway
import java.sql.Connection
import java.sql.ResultSet

class FacilityGatewayImpl(private val connection: Connection) : FacilityGateway {

    private val facilitiesTable = "facilities"
    private val tagsTable = "facilityTags"

    init {
        val facilitiesInit =
            "CREATE TABLE IF NOT EXISTS $facilitiesTable (id string PRIMARY KEY, tags string, createdAt DATETIME DEFAULT CURRENT_TIMESTAMP)"
        val tagsInit = "CREATE TABLE IF NOT EXISTS $tagsTable (facilityId string KEY, tag string)"

        val statement = connection.createStatement()
        statement.execute(facilitiesInit)
        statement.execute(tagsInit)
    }

    private fun buildCreateStatements(facility: Facility): Pair<String, String> {
        val facilityStr =
            "INSERT into $facilitiesTable (id, tags) VALUES ('${facility.id}', '${facility.tags.joinToString()}')"
        val tagStr =
            "INSERT into $tagsTable (facilityId, tag) VALUES ${facility.tags.joinToString { "('${facility.id}', '$it')" }}"
        return Pair(facilityStr, tagStr)
    }

    override fun create(facility: Facility): String {
        val todo = buildCreateStatements(facility)
        val statement = connection.createStatement()
        statement.execute(todo.first)
        statement.execute(todo.second)
        return facility.id
    }


    private fun buildFindStatement(query: FacilitiesQuery): String {
        return """
            SELECT DISTINCT ${facilitiesTable}.* from $tagsTable 
            JOIN $facilitiesTable on ${tagsTable}.facilityId = ${facilitiesTable}.id 
            WHERE tag in ${query.tags.joinToString(prefix = "(", postfix = ")") { "'$it'" }}
        """.trimIndent()
    }

    override fun findFacilities(query: FacilitiesQuery): Sequence<Facility> {
        val findStr = buildFindStatement(query)
        val statement = connection.createStatement()
        val res = statement.executeQuery(findStr)
        return sequence {
            while (res.next()) {
                yield(res.toFacility())
            }
        }
    }

    private fun buildGetStatement(id: String): String = "SELECT * from $facilitiesTable WHERE id = '$id'"

    override fun getFacility(id: String): Facility {
        val getStr = buildGetStatement(id)
        val statement = connection.createStatement()
        val res = statement.executeQuery(getStr)
        if (!res.next()) throw NoSuchFacilityException(id)
        return res.toFacility()
    }

}

fun ResultSet.toFacility(): Facility = Facility(
    getString("id"),
    getString("tags").split(',').map { it.trim() }.toList()
)

