package com.example.ecogo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
class AuthActivity : AppCompatActivity() {
    private var auth=Firebase.auth
    private lateinit var recoverPassword:TextView
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var authButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        recoverPassword=findViewById(R.id.textView10)
        recoverPassword.setOnClickListener(){
            var intent=Intent(this,RecoverPasswordActivity::class.java)
            startActivity(intent)
        }
        authButton=findViewById(R.id.logInButton)
        authButton.setOnClickListener{
            if(validate())
                logIn(email.text.toString(),password.text.toString())
        }
    }
    private fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(getBaseContext(), "Autentificare cu succes!", Toast.LENGTH_LONG).show()
                    var intent:Intent=Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                }else {
                    Toast.makeText(getBaseContext(), "Autentificare esuata", Toast.LENGTH_LONG).show()
                }
            }
    }
  private fun validate():Boolean{
      email=findViewById(R.id.Email)
      password=findViewById(R.id.password)
      if(email.text.toString().isEmpty()){
          email.setError("Campul Email trebuie completat")
         return false
      }
      if(password.text.toString().isEmpty()){
          password.setError("Campul Parola trebuie completat")
          return false
      }
     return true
    }
}