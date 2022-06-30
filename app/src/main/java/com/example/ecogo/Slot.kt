package com.example.ecogo

import android.os.Parcel
import android.os.Parcelable


open class Slot(){
    var type:String=""
    var number=0
    var code:Long=0
    var rented:Boolean=false

    constructor(t:String,n:Int,c:Long):this(){
        type=t
        number=n
        code=c
    }
}