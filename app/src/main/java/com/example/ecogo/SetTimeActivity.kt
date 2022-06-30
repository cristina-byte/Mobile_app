package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*

class SetTimeActivity : AppCompatActivity() {

    private lateinit var nextButton:Button
    private lateinit var pickUpPoint: EditText
    private lateinit var time:Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_time)
        getSupportActionBar()?.setTitle("Rezervă")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        nextButton=findViewById(R.id.next1)
        pickUpPoint=findViewById(R.id.punctRidicare)
        pickUpPoint.setText(intent.getStringExtra("punctRidicare"))
        time=findViewById(R.id.spinner4)
        var activitylauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:
            ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode==100){
                    setResult(100)
                    finish()
                }
            }
        })
        nextButton.setOnClickListener(){
            var timp=time.selectedItem.toString().split(" ").get(0)
                val intent=Intent(this,ScanActivity::class.java)
            var reservation=Reservation(getIntent().getStringExtra("cheiePunctRidicare").toString(),0,
                Date(),0.0,timp.toInt())
            intent.putExtra("reservation",reservation)
               activitylauncher.launch(intent)
        }
    }
}