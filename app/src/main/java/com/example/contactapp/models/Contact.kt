package com.example.contactapp.models

import java.io.Serializable

class Contact: Serializable {
    var name:String? = null
    var number:String? = null

    constructor(name: String?, number: String?) {
        this.name = name
        this.number = number
    }
}