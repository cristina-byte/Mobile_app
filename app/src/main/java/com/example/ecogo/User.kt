package com.example.ecogo

import java.io.Serializable

class User(fName:String, lName:String, _phone:String, _email:String):Serializable {
    var firstName:String=fName
    var lastName:String=lName
    var phone:String=_phone
    var email:String=_email
    var photoLink:String?=null
    var rentedScooters:Int=0
    var rentedBikes:Int=0
    var trips=0
    var hours:Int=0
}