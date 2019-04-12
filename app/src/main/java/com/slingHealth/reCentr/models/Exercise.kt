package com.slingHealth.reCentr.models

import com.google.firebase.database.Exclude
import java.io.Serializable

class Exercise() : Serializable {

    var name: String = ""
    var info: String = ""
    var complete: Boolean = false


    constructor(

        name: String,
        info: String,
        complete: Boolean

    ) : this() {
        this.name = name
        this.info = info
        this.complete = complete
    }

    // [START stat_to_map]
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "info" to info,
            "complete" to complete
        )
    }
}