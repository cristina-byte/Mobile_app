package com.example.ecogo

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import java.util.concurrent.TimeUnit
import android.os.CountDownTimer as CountDownTimer1

class MonitoringActivity : AppCompatActivity() {
    private lateinit var minutes: TextView
    private lateinit var reservedTime:TextView
    private lateinit var price:TextView
    private lateinit var startTime:TextView
    private lateinit var cancelTrip: Button
    private lateinit var intentService:Intent
    private lateinit var progressBar:ProgressBar
    private var currentUserId=FirebaseAuth.getInstance().getCurrentUser()?.getUid().toString()
    private lateinit var reservation:Reservation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitorizare)
        reservation=intent.getSerializableExtra("reservation") as Reservation
        getSupportActionBar()?.setTitle("Monitorizare Călătorie")
        intentService=Intent(this@MonitoringActivity,TripService::class.java)
        progressBar=findViewById(R.id.progressBar)
        progressBar.setProgress(0)
        cancelTrip=findViewById(R.id.stop)
        minutes=findViewById(R.id.time_minutes)
        price=findViewById(R.id.pretCalatorie)
        reservedTime=findViewById(R.id.tf)
        startTime=findViewById(R.id.startDate)
        price.setText(reservation.price.toString())
        reservedTime.setText(reservation.reservedTime.toString())
        startTime.setText(reservation.startDate.hours.toString()+":"+reservation.startDate.minutes)
        var timeInMillis=TimeUnit.MINUTES.toMillis(reservation.reservedTime.toLong())-intent.getLongExtra("continue",0L)
        var activitylauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:
            ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode==100){
                    setResult(100)
                    finish()
                }
            }
        })

        cancelTrip.setOnClickListener{
            var intent=Intent(this,ChooseDestinationActivity::class.java)
            intent.putExtra("reservation",reservation)
            intent.putExtra("keyRezervare",getIntent().getStringExtra("keyRezervare"))
           activitylauncher.launch(intent)
        }


        var flag=0
        var timer=object : CountDownTimer1(timeInMillis, 1000) {
            var millisToWarning=(timeInMillis*20)/100
            override fun onTick(millisUntilFinished: Long) {
                progressBar.setProgress(timeToPercentage(TimeUnit.MINUTES.toMillis(reservation.reservedTime.toLong()),millisUntilFinished))
                if(millisUntilFinished < millisToWarning && flag==0) {
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    intentService.putExtra("punctRidicare",getIntent().getStringExtra("punctRidicare"))
                    intentService.putExtra("time",TimeUnit.MILLISECONDS.toSeconds(millisToWarning))
                    startService(intentService)
                    this@MonitoringActivity.minutes.setTextColor(getResources().getColor(R.color.red3))
                    flag=1
                }
                this@MonitoringActivity.minutes.setText(String.format(Locale.ENGLISH,"%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
            }

            override fun onFinish() {
                Firebase.database.getReference("Users").child(currentUserId).child("Reservations").
                child(getIntent().getStringExtra("keyRezervare").toString()).
                get().addOnSuccessListener {
                    var flag = it.child("finished").getValue(Boolean::class.java)
                    if (flag == false) {
                        var intent = Intent(this@MonitoringActivity, PenaltyActivity::class.java)
                        intent.putExtra("keyRezervare",getIntent().getStringExtra("keyRezervare"))
                        intent.putExtra("reservation",reservation)
                        activitylauncher.launch(intent)
                    }
                }
                stopService(intentService)
            }
        }
        timer.start()
    }

    private fun timeToPercentage(timeMax:Long, time:Long)=((time*100)/timeMax).toInt()
    override fun onBackPressed() {
        Toast.makeText(this,"Aceasta activitate nu poate fi inchisa",Toast.LENGTH_SHORT).show()
    }
}