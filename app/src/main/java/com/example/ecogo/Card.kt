package com.example.ecogo

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class Card() {
     var ownerName=""
     var code=0L
    var expiringDate=0L
    var cvv=""
    var balance=0.0
    var id=""

    constructor(name:String,c:Long,d:Long,cv:String):this(){
        ownerName=name
        code=c
        expiringDate=d
        cvv=cv
    }

    private fun longToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }
    override fun equals(other: Any?): Boolean {
        if(other is Card){
            if(!this.ownerName.equals(other.ownerName))
                return false
            if(!this.code.equals(other.code))
                return false
            if(!longToDate(this.expiringDate).equals(longToDate(other.expiringDate)))
                return false
            if(!this.cvv.equals(other.cvv))
                return false
            return true
        }
return false
    }
}