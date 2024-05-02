//package com.example.easychat
//
//import android.adservices.adid.AdId
//
//class Message {
//    var message: String? = null
//    var senderId:String? = null
//
//    constructor(){}
//
//    constructor(message: String?, senderId: String?){
//        this.message = message
//        this.senderId = senderId
//    }
//}


package com.example.easychat

import java.util.Date // Import Date class
import android.adservices.adid.AdId
class Message {
    var message: String? = null
    var senderId: String? = null
    var timestamp: Long = 0 // Add timestamp field

    constructor() {}

    constructor(message: String?, senderId: String?, timestamp: Long) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }
}
