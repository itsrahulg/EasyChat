package com.example.easychat

class User {
    var name: String? = null
    var email: String? = null
    var phonenumber: String? = null
    var uid: String? = null

    constructor(){

    }

    constructor(name: String?, email: String?,phonenumber: String?,uid: String?){
        this.name = name
        this.email = email
        this.phonenumber = phonenumber
        this.uid = uid
    }

}