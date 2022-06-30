package com.example.ecogo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private var currentUser = FirebaseAuth.getInstance().getCurrentUser()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        getSupportActionBar()?.setTitle("Modifică parola")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        currentPassword = findViewById(R.id.currentPassword)
        newPassword = findViewById(R.id.newPassword)
        saveButton = findViewById(R.id.savePassword)
        cancelButton = findViewById(R.id.cancelPassword)
        saveButton.setOnClickListener() {
            if(validate())
            updatePassword(currentPassword.text.toString(),newPassword.text.toString())
        }
        cancelButton.setOnClickListener{
            currentPassword.setText("")
            newPassword.setText("")
        }
    }

    private fun updatePassword(currentPassword: String, newPassword: String) {
        var credential = EmailAuthProvider.getCredential(currentUser?.email.toString(), currentPassword)
        currentUser!!.reauthenticate(credential)
            .addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful()) {
                        currentUser!!.updatePassword(newPassword)
                            .addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(p0: Task<Void>) {
                                    Toast.makeText(baseContext, "Parola a fost modificata cu succes!", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(baseContext, "Operatiune esuata!", Toast.LENGTH_LONG).show()
                    }
                }
            });
    }

    private fun validate():Boolean{
        if(currentPassword.text.toString().length==0) {
            currentPassword.setError("Acest camp trebuie completat")
            return false
        }

        if(newPassword.text.length==0){
            newPassword.setError("Acest camp trebuie completat")
            return false
        }

        if(newPassword.text.toString().length<6){
            newPassword.setError("Parola trebuie sa contina cel putin 6 caractere")
            return false
        }
        return true
    }





}