package com.colourcog.booking.domain.entity

import java.util.*

data class TimeFrame (val fromDate: Date, val toDate: Date) {
    fun overlaps(other: TimeFrame): Boolean {
        return fromDate <= other.toDate && toDate >= other.fromDate
    }
}