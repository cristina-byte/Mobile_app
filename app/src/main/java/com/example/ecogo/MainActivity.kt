package com.example.ecogo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var authButton: TextView
    private lateinit var registerButton:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authButton=findViewById(R.id.authButton)
        authButton.setOnClickListener{
            var intent:Intent=Intent(this,AuthActivity::class.java)
            startActivity(intent)
        }
        registerButton=findViewById(R.id.registerButton)
        registerButton.setOnClickListener{
            var intent: Intent=Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

    }

}

