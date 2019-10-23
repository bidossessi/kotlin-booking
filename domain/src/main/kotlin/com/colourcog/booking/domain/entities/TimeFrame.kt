package com.colourcog.booking.domain.entities

import com.colourcog.booking.domain.errors.InvalidTimeFrameException
import java.time.LocalDateTime

data class TimeFrame (val dateFrom: LocalDateTime, val dateTo: LocalDateTime) {
    init {
        if (dateFrom >= dateTo) {
            throw InvalidTimeFrameException("$dateFrom is after $dateTo")
        }

    }
    fun overlaps(other: TimeFrame): Boolean {
        return dateFrom <= other.dateTo && dateTo >= other.dateFrom
    }
    fun above(date: LocalDateTime): Boolean {
        return dateFrom > date && dateTo > date
    }
}