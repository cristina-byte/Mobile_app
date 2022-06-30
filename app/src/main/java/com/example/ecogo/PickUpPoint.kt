package com.example.ecogo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PickUpPoint(){
    var name: String = ""
    var imageLink:String=""
    var address: String = ""
    var occupancyRate:Int=0
    var slots=ArrayList<Slot>()
    var coordinates=Point()


    public constructor(n:String,link:String,ad:String,oR:Int,sl:ArrayList<Slot>,c:Point):this(){
        name=n
        imageLink=link
        address=ad
        occupancyRate=oR
        slots=sl
        coordinates=c
    }

}










