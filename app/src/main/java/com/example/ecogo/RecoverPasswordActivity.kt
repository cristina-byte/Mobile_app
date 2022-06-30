package com.example.ecogo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordActivity : AppCompatActivity() {
    private lateinit var sendButton:Button
    private lateinit var email:EditText
    private var auth:FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        email=findViewById(R.id.email)
        sendButton=findViewById(R.id.send)
        sendButton.setOnClickListener(){
            if(email.text.toString().isEmpty()) {
                email.setError("Campul Email trebuie completat")
            }
            else
            recoverPassword(email.text.toString())
            }
    }
    private fun recoverPassword(email:String){
            auth.sendPasswordResetEmail(email).addOnCompleteListener{task->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"Link-ul a fost trimis la adresa specificată",Toast.LENGTH_LONG).show()
                    finish()
                }
                else{
                    Toast.makeText(applicationContext,"Eroare",Toast.LENGTH_LONG).show()
                }
            }
    }
}






