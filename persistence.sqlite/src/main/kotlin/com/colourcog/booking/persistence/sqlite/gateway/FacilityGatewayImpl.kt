package com.colourcog.booking.persistence.sqlite.gateway

import com.colourcog.booking.domain.entity.Facility
import com.colourcog.booking.domain.gateway.FacilitiesQuery
import com.colourcog.booking.domain.gateway.FacilityGateway
import java.sql.Connection
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FacilityGatewayImpl(private val connection: Connection) : FacilityGateway {
    private val facilitiesTable = "facilities"
    private val tagsTable = "facilityTags"

    init {
        val facilitiesInit = "CREATE TABLE IF NOT EXISTS $facilitiesTable (id string PRIMARY KEY, tags string, createdAt DATETIME DEFAULT CURRENT_TIMESTAMP)"
        val tagsInit = "CREATE TABLE IF NOT EXISTS $tagsTable (facilityId string KEY, tag string)"
        val facilityCheck = "SELECT name FROM sqlite_master WHERE type='table' AND name='$facilitiesTable'"
        val tagCheck = "SELECT name FROM sqlite_master WHERE type='table' AND name='$tagsTable'"

        val statement = connection.createStatement()

        val facilityRs = statement.executeQuery(facilityCheck)
        if (!facilityRs.next()) {
            statement.execute(facilitiesInit)
        }
        val tagRs = statement.executeQuery(tagCheck)
        if (!tagRs.next()) {
            statement.execute(tagsInit)
        }
    }

    fun buildCreateStatements(facility: Facility): Pair<String, String> {
        val facilityStr = "INSERT into $facilitiesTable (id, tags) VALUES ('${facility.id}', '${facility.tags.joinToString()}')"
        val tagStr = "INSERT into $tagsTable (facilityId, tag) VALUES ${facility.tags.joinToString { "('${facility.id}', '$it')" }}"
        return Pair(facilityStr,tagStr)
    }

    override fun create(facility: Facility): String {
        val todo = buildCreateStatements(facility)
        val statement = connection.createStatement()
        statement.execute(todo.first)
        statement.execute(todo.second)
        return facility.id
    }

    fun buildFindStatement(query: FacilitiesQuery): String {
        val tags = query.tags.joinToString(prefix = "(", postfix = ")") { "'$it'" }
        return "SELECT DISTINCT ${facilitiesTable}.* from $tagsTable JOIN $facilitiesTable on ${tagsTable}.facilityId = ${facilitiesTable}.id WHERE tag in $tags"
    }

    override fun findFacilities(query: FacilitiesQuery): Sequence<Facility> {
        val find = buildFindStatement(query)
        val statement = connection.createStatement()
        val res = statement.executeQuery(find)
        return sequence {
            while (res.next()) {
                yield(res.toFacility())
            }
        }
    }
}

data class FindFacilityLine(val id: String, val tag: String, val createdAd: Date)

fun ResultSet.toFacility(): Facility = Facility(
    getString("id"),
    getString("tags").split(',')
)

