package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PickUpPointsActivity : AppCompatActivity() {

    private lateinit var pickUpPointsLayout:LinearLayout
    private var pointsReference=Firebase.database.getReference("Pick-upPoints")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pick_up_points)
        getSupportActionBar()?.setTitle("Puncte de ridicare")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        pickUpPointsLayout=findViewById(R.id.puncte)
        parse()
    }
    private fun rentedSlots(slots:ArrayList<Slot>):Int{
        var nr=0
        for(slot in slots){
            if(slot.rented)
                nr++
        }
        return nr
    }
    private fun addItem(point:PickUpPoint, pointKey:String){
        var view=layoutInflater.inflate(R.layout.pickup_point,null)
        var title=view.findViewById<TextView>(R.id.unitate_title)
        var occupancyReport=view.findViewById<TextView>(R.id.number)
        var photo= view.findViewById<ImageView>(R.id.imageViewnou)
        var card=view.findViewById<CardView>(R.id.card_grad)
        var address= view.findViewById<TextView>(R.id.adress)
        Glide.with(getApplicationContext()).load(point.imageLink).into(photo)
        title.setText(point.name)
        address.setText("str. "+point.address)
        occupancyReport.setText(rentedSlots(point.slots).toString()+"/"+point.slots.size)
        if(point.occupancyRate>95)
            card.setCardBackgroundColor(getResources().getColor(R.color.red2))
        pickUpPointsLayout.addView(view)
        view.setOnClickListener{
            var intent= Intent(this,ViewPickUpPointActivity::class.java)
            intent.putExtra("cheiePunctRidicare",pointKey)
            intent.putExtra("punctRidicare",title.text.toString())
            intent.putExtra("latitudinePunct",point.coordinates.latitude)
            intent.putExtra("longitudinePunct",point.coordinates.longitude)
            this.startActivity(intent)
        }
    }
    private fun getSlots(pickUpPoint:DataSnapshot):ArrayList<Slot> {
        var slots= arrayListOf<Slot>()
        for(s in pickUpPoint.child("slots").children){
            var slot=s.getValue(Slot::class.java)
            slots.add(slot!!)
        }
        return slots
    }

    private fun parse(){
     pointsReference.addValueEventListener(object:ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
             pickUpPointsLayout.removeAllViews()
             for(p in snapshot.children){
                 var slots=getSlots(p)
                 var punct=PickUpPoint(p.child("name").getValue(String::class.java).toString(),p.child("imageLink").
                 getValue(String::class.java).toString(),p.child("address").
                 getValue(String::class.java).toString(),p.child("occupancyRate").
                 getValue(Int::class.java)!!.toInt(),slots,p.child("coordinates").getValue(Point::class.java)!!)
                 addItem(punct,p.key.toString())
             }
         }
         override fun onCancelled(error: DatabaseError) {}
     })
  }


}



