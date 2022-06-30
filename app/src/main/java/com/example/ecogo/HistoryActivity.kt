package com.example.ecogo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class HistoryActivity : AppCompatActivity() {
    private var usersReference= Firebase.database.getReference("Users")
    private lateinit var bikesNumber:TextView
    private lateinit var scootersNumber:TextView
    private lateinit var tripsNumber:TextView
    private lateinit var hours:TextView
    private lateinit var mostActiveDay:TextView
    private var currentUserId=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var reservationsList:LinearLayout
    private lateinit var unfinishedReservationsList:LinearLayout
    private var months= arrayOf("JAN","FEB","MART","APR","MAI","JUN","JUL","AUG","SEPT","OCT","NOV","DEC")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setTitle("Călătoriile mele")
        bikesNumber=findViewById(R.id.trotineteInchiriate)
        scootersNumber=findViewById(R.id.bicicleteInchiriate)
        hours=findViewById(R.id.oreCalatorii)
        tripsNumber=findViewById(R.id.nrCalatorii)
        mostActiveDay=findViewById(R.id.textView80)
        reservationsList=findViewById(R.id.rezervari)
        unfinishedReservationsList=findViewById(R.id.rezervari_nefinisate)
        getStatistics()
        printReservations()

    }



    private fun getStatistics(){
       usersReference.child(currentUserId).
        addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bikesNumber.setText(snapshot.child("rentedScooters").getValue(Int::class.java).toString())
                scootersNumber.setText(snapshot.child("rentedBikes").getValue(Int::class.java).toString())
                var seconds=snapshot.child("hours").getValue(Int::class.java)!!.toInt()
                var hours=seconds/3600
                var minutes=(seconds%3600)/60
                this@HistoryActivity.hours.setText(hours.toString()+" h "+minutes+" m")
                tripsNumber.setText(snapshot.child("trips").getValue(Int::class.java).toString())
                getMostActiveDay()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }




    private fun getMostActiveDay(){

        var date= arrayListOf<ModelRezervare>()
        usersReference.child(currentUserId).child("Reservations").get().addOnSuccessListener {
                for(reservation in it.children){
                    var d=reservation.child("startDate").getValue(Date::class.java)
                    if(!exists(d,date,reservation.child("time").getValue(Int::class.java)))
                        date.add(ModelRezervare(d!!,reservation.child("time").getValue(Int::class.java)!!))
                }
                if(date.size>0) {
                    var max = date[0]
                    for (rez in date) {
                        if (rez.timp > max.timp)
                            max = rez
                    }
                    mostActiveDay.setText(max.date.date.toString() + " " + months[max.date.month])
                }
                else{ mostActiveDay.setText(" ") }
            }
    }

    private fun exists(d: Date?, date: ArrayList<ModelRezervare>, durata:Int?): Boolean {
        for(i in 0..date.count()-1){
            if(d?.date==date[i].date.date && d.month==date[i].date.month && d.year==date[i].date.year){
                date[i].timp=date[i].timp+durata!!
                return true
            }
        }
        return false
    }


    private fun printReservations(){
        usersReference.child(currentUserId).child("Reservations").
        addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationsList.removeAllViews()
                unfinishedReservationsList.removeAllViews()
                for(reservation in snapshot.children){
                    var r=reservation.getValue(Reservation::class.java)
                    if(r!=null){
                        if(r.finished==true)
                            addReservation(r)
                        else
                            addUnfinishedReservation(r)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }



    private fun addReservation(reservation: Reservation) {
        val view=layoutInflater.inflate(R.layout.reservation,null)
        view.findViewById<TextView>(R.id.textView46).setText(reservation.startDate.hours.toString()+":"+reservation.startDate.minutes)
        view.findViewById<TextView>(R.id.textView48).setText(reservation.price.toInt().toString()+" RON")
        view.findViewById<TextView>(R.id.penalizare).setText(reservation.penalty.toString()+" RON")
            val minutes=reservation.time/60
            val seconds=reservation.time%60
            view.findViewById<TextView>(R.id.textView47).setText(minutes.toString() + " MIN "+seconds.toString()+" SEC")
        view.findViewById<TextView>(R.id.textView44).setText(months[reservation.startDate.month]+reservation.startDate.getDate().toString())
        Firebase.database.getReference("Pick-upPoints").child(reservation.pickUpPoint).child("name").get().addOnSuccessListener {
            view.findViewById<TextView>(R.id.textView27).setText(it.getValue(String::class.java).toString())
        }
        Firebase.database.getReference("Pick-upPoints").child(reservation.destinationPoint).child("name").get().addOnSuccessListener {
            view.findViewById<TextView>(R.id.textView45).setText(it.getValue(String::class.java).toString())
        }
        reservationsList.addView(view!!)
    }



   private fun addUnfinishedReservation(reservation:Reservation) {
       val view = layoutInflater.inflate(R.layout.unfinished_reservation, null)
       view.findViewById<TextView>(R.id.textView46)
           .setText(reservation.startDate.hours.toString() + ":" + reservation.startDate.minutes)
       view.findViewById<TextView>(R.id.textView44).setText(
           months[reservation.startDate.month] + reservation.startDate.getDate().toString()
       )
       unfinishedReservationsList.addView(view!!)
       Firebase.database.getReference("Pick-upPoints").child(reservation.pickUpPoint).child("name")
           .get().addOnSuccessListener {
           view.findViewById<TextView>(R.id.textView27)
               .setText(it.getValue(String::class.java).toString())
       }

       var continueButton: Button = view.findViewById(R.id.continue_button)
       continueButton.setOnClickListener {
           var cD = Calendar.getInstance()
           var sD = Calendar.getInstance()
           sD.setTime(reservation.startDate)
           var dif = cD.timeInMillis - sD.timeInMillis
           usersReference.child(currentUserId).child("Reservations").get().addOnSuccessListener {
               for (r in it.children) {
                   if (r.child("finished").getValue(Boolean::class.java) == false) {
                       if (TimeUnit.MILLISECONDS.toMinutes(dif) > reservation.reservedTime) {
                           reservation.penalty = ((dif /900000)* 2).toDouble()
                           var intent = Intent(this, PenaltyActivity::class.java)
                           intent.putExtra("keyRezervare", r.key)
                           intent.putExtra("reservation", reservation)
                           startActivity(intent)
                       }
                       else {
                           var intent= Intent(this,MonitoringActivity::class.java)
                           intent.putExtra("keyRezervare",r.key)
                           intent.putExtra("continue",dif)
                           intent.putExtra("reservation",reservation)
                           startActivity(intent)
                       }
                   }
               }
           }
       }
   }
}