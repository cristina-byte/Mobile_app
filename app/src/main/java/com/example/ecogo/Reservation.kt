package com.example.ecogo
import java.io.Serializable
import java.util.*

class Reservation ():Serializable {
    var pickUpPoint:String=""
    var destinationPoint:String=""
    var slotKey=""
    var slotKeyDestination=""
    var penalty:Double=0.0
    var price:Double=0.0
    var startDate: Date=Date()
    var time:Int=0
    var finished=false
    var reservedTime=0
    var transportType=""


    constructor( pK:String, t:Int, sD:Date, pr:Double,rT:Int):this(){
        pickUpPoint=pK
        price=pr
        startDate=sD
        time=t
        reservedTime=rT
    }



}


