package com.example.ecogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var changePassword:Button
    private lateinit var changePhoto:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cont)
        getSupportActionBar()?.setTitle("Setari Cont")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        changePassword=findViewById<Button>(R.id.password_settings)
        changePhoto=findViewById<Button>(R.id.photo_change)
        changePassword.setOnClickListener(){
            var intent= Intent(this,ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        changePhoto.setOnClickListener(){
            var intent=Intent(this,AddPhotoActivity::class.java)
            startActivity(intent)
        }
    }
}