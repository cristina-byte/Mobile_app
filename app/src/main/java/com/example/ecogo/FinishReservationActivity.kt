package com.example.ecogo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FinishReservationActivity : AppCompatActivity() {
    private lateinit var closeButton:Button
    private var currentUserId= FirebaseAuth.getInstance().getCurrentUser()?.getUid().toString()
    private lateinit var reservation:Reservation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        reservation=intent.getSerializableExtra("reservation") as Reservation
        closeButton=findViewById(R.id.cancel)
        closeButton.setOnClickListener(){
            setResult(100)
            finish()
        }
        updateStatus()
        finishReservation(getIntent().getStringExtra("keyRezervare").toString())
        updateStatistics()
    }
    private fun getReserved(slots: DataSnapshot): Int{
        var nr=0
        for(s in slots.children){
            if(s.child("rented").getValue(Boolean::class.java)==true)
                nr++;
        }
        return nr
    }
    private fun finishReservation(reservationKey:String){
        Firebase.database.getReference("Users").child(currentUserId).child("Reservations").
        child(getIntent().getStringExtra("keyRezervare").toString()).setValue(reservation)
        Firebase.database.getReference("Users").child(currentUserId).child("Reservations").
        child(reservationKey).child("finished").setValue(true)
    }
    private fun updateStatus(){
        Firebase.database.getReference("Pick-upPoints").child(reservation.destinationPoint).
        child("slots").child(reservation.slotKeyDestination).child("rented").setValue(false)
        var gradOcupare=0.0
        Firebase.database.getReference("Pick-upPoints").
        child(reservation.destinationPoint).child("slots").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var numarUnitati=snapshot.children.count()
                var numarUnitatiRezervate=getReserved(snapshot)
                gradOcupare=(numarUnitatiRezervate*100)/numarUnitati.toDouble()
                Firebase.database.getReference("Pick-upPoints").
                child(reservation.destinationPoint).child("occupancyRate").setValue(gradOcupare)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun updateStatistics(){
        Firebase.database.getReference("Users").child(currentUserId).get().addOnSuccessListener {
                var nrTrotinete=it.child("rentedScooters").getValue(Int::class.java)!!.toInt()
                var nrBiciclete=it.child("rentedBikes").getValue(Int::class.java)!!.toInt()
                var nrCalatorii=it.child("trips").getValue(Int::class.java)!!.toInt()
                var oreCalatorii=it.child("hours").getValue(Int::class.java)!!
                var timp=reservation.time
                Firebase.database.getReference("Users").child(currentUserId).child("trips").setValue(nrCalatorii+1)
                Firebase.database.getReference("Users").child(currentUserId).
                child("hours").setValue(oreCalatorii+timp)
                Firebase.database.getReference("Users").child(currentUserId).child("Reservations").
                child(getIntent().getStringExtra("keyRezervare").toString()).child("transportType").get().addOnSuccessListener {
                    var type=it.getValue(String::class.java)
                    if(type.equals("trotineta")){
                        Firebase.database.getReference("Users").child(currentUserId).
                        child("rentedScooters").setValue(nrTrotinete+1)
                    }
                    else if(type.equals("bicicleta")){
                        Firebase.database.getReference("Users").child(currentUserId).
                        child("rentedBikes").setValue(nrBiciclete+1)
                    }
                }
            }
    }
}