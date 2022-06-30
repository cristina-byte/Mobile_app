package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {
    private lateinit var sold:TextView
    private lateinit var cardNumber:TextView
    private lateinit var expiringDate: TextView
    private lateinit var reservedTime:TextView
    private lateinit var totalPrice:TextView
    private lateinit var priceForHour:TextView
    private lateinit var finishButton:Button
    private var cardsReference=Firebase.database.getReference("Cards")
    private var pointsReference=Firebase.database.getReference("Pick-upPoints")
    private var currentUserId=FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var activitylauncher:ActivityResultLauncher<Intent?>
    private lateinit var reservation:Reservation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paying)
        reservation=intent.getSerializableExtra("reservation") as Reservation
        getSupportActionBar()?.setTitle("Plata")
        reservedTime=findViewById(R.id.textView68)
        reservedTime.setText(reservation.reservedTime.toString())
        totalPrice=findViewById(R.id.price)
        priceForHour=findViewById(R.id.textView65)
        sold=findViewById(R.id.sold)
        cardNumber=findViewById(R.id.cardNumber)
        finishButton=findViewById(R.id.finish)
        expiringDate=findViewById(R.id.expireDate)

            if (reservation.transportType == "trotineta")
                priceForHour.setText("6")
            else if (reservation.transportType == "bicicleta")
                priceForHour.setText("4")

            var price=calculatePrice(reservedTime.text.toString().toInt(),reservation.transportType)
            totalPrice.setText(price.toString())
            reservation.price=price

        getData()
        activitylauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:
            ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode==100){
                    setResult(100)
                    finish()
                }
            }
        })
        finishButton.setOnClickListener() {
                var price=calculatePrice(reservedTime.text.toString().toInt(), reservation.transportType)
                makePayment(price)
        }
    }


    private fun updateStatus(){
        pointsReference.child(reservation.pickUpPoint).
        child("slots").
        child(reservation.slotKey).child("rented").setValue(true)
        var occupancyRate=0.0
        pointsReference.
        child(reservation.pickUpPoint).child("slots").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numarUnitati=snapshot.children.count()
                var numarUnitatiRezervate=getReserved(snapshot)
                occupancyRate=(numarUnitatiRezervate*100)/numarUnitati.toDouble()
                pointsReference.
                child(reservation.pickUpPoint).child("occupancyRate").setValue(occupancyRate)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }





    private fun getReserved(slots: DataSnapshot): Int{
        var nr=0
        for(slot in slots.children){
           if(slot.child("rented").getValue(Boolean::class.java)==true)
               nr++;
        }
        return nr
    }


    private fun getData(){
        Firebase.database.getReference("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).
        child("Card").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var cardKey=snapshot.getValue(String::class.java)
                cardsReference.child(cardKey!!).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        sold.setText(snapshot.child("balance").getValue(Double::class.java).toString()+" RON")
                        cardNumber.setText(snapshot.child("code").getValue(Long::class.java).toString())
                        expiringDate.setText(longToDate(snapshot.child("expiringDate").getValue(Long::class.java)!!).toString())
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun longToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }


    private fun makePayment(price:Double){
        var balance=0.0
        var reference=Firebase.database.getReference("Users")
        reference.child(currentUserId.toString()).child("Card").get().addOnSuccessListener{
                var cardKey=it.getValue(String::class.java)
                cardsReference.child(cardKey.toString()).child("balance").get().addOnSuccessListener {
                    balance = it.getValue(Double::class.java)!!.toDouble()
                    if (balance < price)
                        Toast.makeText(this@PaymentActivity, "Nu exista suficienti banuti in cont", Toast.LENGTH_LONG).show()
                    else {
                        Firebase.database.getReference("Cards").child(cardKey!!).child("balance").setValue(balance - price)
                        updateStatus()
                        var currentDate=Calendar.getInstance()
                        var date:Date=currentDate.getTime()
                        reservation.startDate=date
                        var keyReservation=Firebase.database.getReference("Users").child(currentUserId.toString()).child("Rezervari").push().key.toString()
                        Firebase.database.getReference("Users").child(currentUserId.toString()).child("Reservations").child(keyReservation).setValue(reservation).addOnSuccessListener {
                            var intent=Intent(this,MonitoringActivity::class.java)
                            intent.putExtra("reservation",reservation)
                            intent.putExtra("keyRezervare",keyReservation)
                            activitylauncher.launch(intent)
                        }
                    }
                }
        }
    }

    private fun calculatePrice(time:Int, tip:String):Double{
        var totalPrice=0.0
        if(tip=="bicicleta")
            totalPrice=((time * 4) / 60).toDouble()
        else if(tip=="trotineta")
            totalPrice=((time*6)/60).toDouble()
        return totalPrice
    }
}

