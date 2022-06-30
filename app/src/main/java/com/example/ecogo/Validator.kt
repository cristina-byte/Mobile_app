package com.example.ecogo

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Validator {
    fun isPhoneNumber(sir:String): Boolean {
        var pattern= Pattern.compile("[0-9]{10}")
        var matcher=pattern.matcher(sir)
        return matcher.matches()
    }

    fun isValidDate(sir:String):Boolean{
        val calendar=Calendar.getInstance()
        val sdformat = SimpleDateFormat("yyyy.MM.dd")
        val d1: Date = sdformat.parse(sir)
        var currentDate=calendar.get(Calendar.YEAR).toString()+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH)
        val d2: Date = sdformat.parse(currentDate)
        if(d1.compareTo(d2)<0){
            println("data aleasa este mai mica decat data de azi")
            return false
        }
        return true
    }
    fun isValidDate2(sir:String):Boolean{
        val calendar=Calendar.getInstance()
        val sdformat = SimpleDateFormat("yyyy.MM.dd")
        val d1: Date = sdformat.parse(sir)
        var currentDate=calendar.get(Calendar.YEAR).toString()+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH)
        val d2: Date = sdformat.parse(currentDate)
        if(d1.compareTo(d2)>0){
            println("data aleasa este mai mare decat data de azi")
            return false
        }
        return true
    }





}