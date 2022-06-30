package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChooseDestinationActivity : AppCompatActivity() {

    private lateinit var destinationPoint: Spinner
    private lateinit var nextButton: Button
    private lateinit var arrayList:ArrayList<String>
    private var pointsReference=Firebase.database.getReference("Pick-upPoints")
    private lateinit var reservation:Reservation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_point)
        reservation=intent.getSerializableExtra("reservation") as Reservation
        arrayList= arrayListOf()
        destinationPoint=findViewById(R.id.punctRidicare2)
        var adapter=ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arrayList)
        destinationPoint.setAdapter(adapter)
        addItems(adapter)

        var activitylauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:
            ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode==100){
                    setResult(100)
                    finish()
                }

            }
        })

        nextButton=findViewById(R.id.next2)
        nextButton.setOnClickListener{
            var nume=destinationPoint.selectedItem.toString()
            pointsReference.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(p in snapshot.children){
                        if(p.child("name").getValue(String::class.java).equals(nume)){
                            var intent = Intent(this@ChooseDestinationActivity, ReturnScanActivity::class.java)
                            var cod=p.key
                            reservation.destinationPoint=cod.toString()
                            intent.putExtra("reservation",reservation)
                            intent.putExtra("keyRezervare",getIntent().getStringExtra("keyRezervare"))
                            activitylauncher.launch(intent)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun addItems(adapter: ArrayAdapter<String>) {
        Firebase.database.getReference("Pick-upPoints").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(p in snapshot.children){
                    if(p.child("occupancyRate").getValue(Double::class.java)!!.toDouble()>0) {
                        val punct = p.child("name").getValue(String::class.java).toString()
                        arrayList.add(punct)
                        adapter.notifyDataSetChanged()
                        destinationPoint.setAdapter(adapter)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}