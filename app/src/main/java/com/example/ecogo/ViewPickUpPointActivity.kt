package com.example.ecogo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ViewPickUpPointActivity : AppCompatActivity() {

    private lateinit var pointMap:SupportMapFragment
    private lateinit var googleMap:GoogleMap
    private lateinit var bikes: LinearLayout
    private lateinit var scooters:LinearLayout
    private lateinit var reserveButton:Button
    private var numberOfBikes=0
    private var numberOfScooters=0
    private var pointsReference=Firebase.database.getReference("Pick-upPoints")
    private lateinit var occupancyRate:TextView
    private lateinit var title:TextView
    private lateinit var pointKey:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punct_ridicare)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        bikes=findViewById(R.id.biciclete)
        scooters=findViewById(R.id.trotinete)
        occupancyRate=findViewById(R.id.grad)
        title=findViewById(R.id.titlu_punctRidicare)
        pointKey=intent.getStringExtra("cheiePunctRidicare").toString()
        getData()
        pointMap = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        pointMap.getMapAsync(
            OnMapReadyCallback {
                googleMap=it
                var latitudine=intent.getDoubleExtra("latitudinePunct",0.0)
                var longitudine=intent.getDoubleExtra("longitudinePunct",0.0)
                val locatie=LatLng(latitudine,longitudine)
                googleMap.addMarker(MarkerOptions().position(locatie).title("PunctRidicare"))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locatie,18f))
                googleMap.uiSettings.setZoomControlsEnabled(true)
            })
        reserveButton=findViewById(R.id.rezervaTransport2)
        reserveButton.setOnClickListener(){
            check()
        }
        printSlots()
    }


    private fun getData(){
        pointsReference.child(pointKey).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
             occupancyRate.setText(snapshot.child("occupancyRate").getValue(Int::class.java).toString())
                title.setText(snapshot.child("name").getValue(String::class.java))
                getSupportActionBar()?.setTitle(snapshot.child("name").getValue(String::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }



    private fun check(){
        Firebase.database.getReference("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).
        child("Card").get().addOnSuccessListener {
            var cardKey=it.getValue(String::class.java)
            if(cardKey!=null) {
                Firebase.database.getReference("Users").
                child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("Reservations").get().addOnSuccessListener {
                    var flag=0
                    for (r in it.children) {
                        if (r.child("finished").getValue(Boolean::class.java) == false) {
                           Toast.makeText(this,"Aveti o rezervare nefinisata.",Toast.LENGTH_SHORT).show()
                            flag=1
                            break
                        }
                    }
                    if(flag==0){
                        var intent=Intent(this,SetTimeActivity::class.java)
                        intent.putExtra("punctRidicare",getIntent().getStringExtra("punctRidicare"))
                        intent.putExtra("cheiePunctRidicare",getIntent().getStringExtra("cheiePunctRidicare").toString())
                        startActivity(intent)
                    }
                }
            }
            else
                Toast.makeText(this,"Aveti nevoie de card pentru a rezerva",Toast.LENGTH_LONG).show()
        }
    }


    private fun printSlots(){
        var mijloaceDisponibile=0
        numberOfBikes=0
        numberOfScooters=0
        var pointReference=pointsReference.child(pointKey).child("slots")
        pointReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bikes.removeAllViews()
                scooters.removeAllViews()
                numberOfScooters=0
                numberOfBikes=0
                for(slot in snapshot.children){
                    addSlot(slot)
                    if(slot.child("rented").getValue(Boolean::class.java)==false)
                        mijloaceDisponibile++
                }
                findViewById<TextView>(R.id.nr_biciclete).setText(numberOfBikes.toString())
                findViewById<TextView>(R.id.nr_trotinete).setText(numberOfScooters.toString())
                if( (numberOfScooters==0 && numberOfBikes==0) || mijloaceDisponibile==0){
                    reserveButton.setEnabled(false)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }




    private fun addBikeSlot(s:DataSnapshot){
        var view=layoutInflater.inflate(R.layout.bike_slot,null)
        var status:TextView=view.findViewById<TextView>(R.id.status)
        var cardBackground=view.findViewById<CardView>(R.id.cardSlotB)
        if(s.child("rented").getValue(Boolean::class.java)==true){
            status.setText("Ocupat")
            status.setTextColor(resources.getColor(R.color.red))
            cardBackground.setCardBackgroundColor(resources.getColor(R.color.pink))
        }
        else{
            status.setText("Liber")
            status.setTextColor(resources.getColor(R.color.teal_700))
            cardBackground.setCardBackgroundColor(resources.getColor(R.color.green3))
        }
        view.findViewById<TextView>(R.id.unitate_title).setText("Slot "+s.child("number").getValue(Int::class.java))
        bikes.addView(view)
    }





    private fun addScooterSlot(s:DataSnapshot){
        var view=layoutInflater.inflate(R.layout.scooter_slot,null)
        var status:TextView=view.findViewById<TextView>(R.id.status)
        var cardBackground=view.findViewById<CardView>(R.id.cardSlotT)
        if(s.child("rented").getValue(Boolean::class.java)==true){
            status.setText("Ocupat")
            status.setTextColor(resources.getColor(R.color.red))
            cardBackground.setCardBackgroundColor(resources.getColor(R.color.pink))
        }
        else{
            status.setText("Liber")
            status.setTextColor(resources.getColor(R.color.teal_700))
            cardBackground.setCardBackgroundColor(resources.getColor(R.color.green3))
        }
        view.findViewById<TextView>(R.id.unitate_title).setText("Slot "+s.child("number").getValue(Int::class.java))
        scooters.addView(view)
    }


    private fun addSlot(s:DataSnapshot){
        var tip=s?.child("type")?.getValue(String::class.java)
        if(tip=="bicicleta") {
            numberOfBikes++
            addBikeSlot(s)
        }
        else if(tip=="trotineta") {
            numberOfScooters++
            addScooterSlot(s)
        }
    }
}