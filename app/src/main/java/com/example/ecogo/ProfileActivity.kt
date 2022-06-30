package com.example.ecogo
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private var currentUserId=FirebaseAuth.getInstance().getCurrentUser()?.getUid().toString()
    private var reference= Firebase.database.getReference("Users").child(currentUserId)
    private lateinit var dialog:Dialog
    private lateinit var okButton:Button
    private lateinit var cancelButton:Button
    private lateinit var historyButton:Button
    private lateinit var helpButton:Button
    private lateinit var reportProblemButton:Button
    private lateinit var pointsButton:Button
    private lateinit var settingsButton:Button
    private lateinit var logOutButton:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        getData()
        historyButton=findViewById(R.id.trips)
        helpButton=findViewById(R.id.help)
        reportProblemButton=findViewById(R.id.report)
        pointsButton=findViewById(R.id.points)
        settingsButton=findViewById(R.id.settings)
        logOutButton=findViewById<Button>(R.id.logout)

        historyButton.setOnClickListener{
            var intent:Intent=Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }
        helpButton.setOnClickListener{
            val intent=Intent(this,HelpActivity::class.java)
            startActivity(intent)
        }
        reportProblemButton.setOnClickListener{
            val intent=Intent(this,ReportProblemActivity::class.java)
            startActivity(intent)
        }
        pointsButton.setOnClickListener{
            val intent=Intent(this,PickUpPointsActivity::class.java)
            startActivity(intent)
        }
        settingsButton.setOnClickListener{
            var intent=Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }
        logOutButton.setOnClickListener(){
            dialog.show()
        }

        dialog=Dialog(this)
        dialog.setContentView(R.layout.dialog)
        dialog.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.background_dialog))
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        okButton=dialog.findViewById(R.id.da)
        cancelButton=dialog.findViewById(R.id.no)
        okButton.setOnClickListener(){
            dialog.cancel()
            this.finish()
        }
        cancelButton.setOnClickListener(){
            dialog.cancel()
        }
    }

    private fun getData(){

        reference.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(dataSnapshot:DataSnapshot) {
                var firstName=dataSnapshot.child("firstName").getValue(String::class.java)
                var lastName=dataSnapshot.child("lastName").getValue(String::class.java)
                var photoLink=dataSnapshot.child("photoLink").getValue(String::class.java)
                var photoView=findViewById<ImageView>(R.id.photo)
                Glide.with(applicationContext).load(photoLink).into(photoView)
                var email=FirebaseAuth.getInstance().getCurrentUser()?.getEmail()
                findViewById<TextView>(R.id.numeUser).setText(firstName+" "+lastName)
                findViewById<TextView>(R.id.emailUser).setText(email)
            }
            override fun onCancelled(error: DatabaseError) {}


      })
    }

}