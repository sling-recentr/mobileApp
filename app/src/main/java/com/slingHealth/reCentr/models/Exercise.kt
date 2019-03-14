package com.slingHealth.reCentr.models

import java.io.Serializable

class Exercise() : Serializable {

    var name: String = ""
    var info: String = ""


    constructor(

        name: String,
        info: String

    ) : this() {
        this.name = name
        this.info = info
    }
}