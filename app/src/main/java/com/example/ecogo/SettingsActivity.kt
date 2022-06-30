package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {

    private lateinit var accountSettings:Button
    private lateinit var cardSettings:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        getSupportActionBar()?.setTitle("Setări")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        accountSettings=findViewById(R.id.cont_item)
        cardSettings=findViewById(R.id.card_item)
        accountSettings.setOnClickListener(){
            var intent=Intent(this,AccountSettingsActivity::class.java)
            startActivity(intent)
        }
       cardSettings.setOnClickListener(){
            var intent= Intent(this,CardSettingsActivity::class.java)
            startActivity(intent)
        }
    }
}