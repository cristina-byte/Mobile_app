package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer as CountDownTimer1
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class PenaltyActivity : AppCompatActivity() {

    private lateinit var finishButton: Button
    private var currentUserId=FirebaseAuth.getInstance().getCurrentUser()?.getUid().toString()
    private lateinit var reservation:Reservation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penalty)
        reservation=intent.getSerializableExtra("reservation") as Reservation
        finishButton=findViewById(R.id.finish2)
        var activitylauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:
            ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode==100){
                    setResult(100)
                    finish()
                }
            }
        })

        finishButton.setOnClickListener{
            var intent=Intent(this,ChooseDestinationActivity::class.java)
            intent.putExtra("reservation",reservation)
            intent.putExtra("keyRezervare",getIntent().getStringExtra("keyRezervare"))
            activitylauncher.launch(intent)
        }


        var timeInMillis=(900000).toLong()
        var timer=object : CountDownTimer1(timeInMillis , 1000) {
            var serviceIntent=Intent(this@PenaltyActivity,PenaltyService::class.java)
            var f=0
            fun startCounting(){
                start()
            }
            override fun onTick(millisUntilFinished: Long) {
                if((timeInMillis-millisUntilFinished).toInt()>9000 && f==0 && reservation.penalty>0) {
                    stopService(serviceIntent)
                    f=1
                }
            }
            override fun onFinish() {
                Firebase.database.getReference("Users").child(currentUserId!!).child("Reservations").
                child(getIntent().getStringExtra("keyRezervare").toString()).get().addOnSuccessListener{
                        var flag = it.child("finished").getValue(Boolean::class.java)
                        if (flag == false) {
                            reservation.penalty=reservation.penalty+2
                            penalizare()
                            startService(serviceIntent)
                            f=0
                            startCounting()
                        }
                        else stopService(serviceIntent)
                    }
            }
        }
            timer.start()
    }
       private fun penalizare(){
           Firebase.database.getReference("Users").child(currentUserId!!).child("Card").
           addListenerForSingleValueEvent(object:ValueEventListener{
               override fun onDataChange(snapshot: DataSnapshot) {
                   var cardKey=snapshot.getValue(String::class.java)
                   if(cardKey!=null) {
                       Firebase.database.getReference("Cards").child(cardKey).child("balance")
                           .addListenerForSingleValueEvent(object : ValueEventListener {
                               override fun onDataChange(snapshot: DataSnapshot) {
                                   var disponibil = snapshot.getValue(Double::class.java)
                                   Firebase.database.getReference("Cards").child(cardKey)
                                       .child("balance").setValue(disponibil!!.toDouble() - 2)
                               }
                               override fun onCancelled(error: DatabaseError) {}
                           })
                   }
               }
               override fun onCancelled(error: DatabaseError) {}
           })
        }
    override fun onBackPressed() {
        Toast.makeText(this,"Aceasta activitate nu poate fi inchisa", Toast.LENGTH_SHORT).show()
    }


}