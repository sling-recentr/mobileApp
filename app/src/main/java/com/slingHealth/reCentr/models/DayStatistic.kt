package com.slingHealth.reCentr.models

import com.google.firebase.database.Exclude


class DayStatistic {

    var data: MutableList<Double> = MutableList(24) { 0.0 }
    var total: Int = 0
    var maximum: Double = 0.00

    // [START stat_to_map]
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "data" to data,
            "total" to total,
            "maximum" to maximum
        )
    }
    // [END stat_to_map]
}