package com.example.ecogo
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
class RegisterActivity : AppCompatActivity() {

    private var auth: FirebaseAuth=Firebase.auth
    private lateinit var addCard:Button
    private lateinit var lastName:EditText
    private lateinit var firstName:EditText
    private lateinit var email:EditText
    private lateinit var phone:EditText
    private lateinit var password:EditText
    private lateinit var registerButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        lastName=findViewById(R.id.editTextName)
        firstName=findViewById(R.id.editTextPrenume)
        email=findViewById(R.id.editTextEmail)
        phone=findViewById(R.id.editTextTelefon)
        password=findViewById(R.id.editTextParola)
        addCard=findViewById(R.id.addCard)
        addCard.setEnabled(false)
        addCard.setOnClickListener(){
            var intent=Intent(this,AddCardActivity::class.java)
            startActivity(intent)
        }
        registerButton=findViewById(R.id.addUser)
        registerButton.setOnClickListener{
            if(validate()==true){
                var user=User(lastName.text.toString(),firstName.text.toString(),phone.text.toString(),email.text.toString())
               registerUser(email.text.toString(),password.text.toString(),user)
            }
        }
    }

    private fun registerUser(email: String, password: String, user:User) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid).setValue(user);
                    Toast.makeText(baseContext, "Utilizatorul a fost inregistrat cu succes!", Toast.LENGTH_LONG).show()
                    addCard.setEnabled(true)
                } else {
                    Toast.makeText(baseContext, "Inregistrarea a eșuat", Toast.LENGTH_LONG).show()
                }
            }

    }



    private fun validate():Boolean{
        val validator=Validator()
        if(lastName.text.toString().isEmpty()) {
            lastName.setError("Campul Nume trebuie completat")
            return false
        }
        if(firstName.text.toString().isEmpty()) {
            firstName.setError("Campul Prenume trebuie completat")
            return false
        }
        if(email.text.toString().isEmpty()) {
            email.setError("Campul Email trebuie completat")
            return false
        }
        if(!email.text.toString().contains('@')){
            email.setError("Campul Email trebuie să conțină o adresă de email validă")
            return false
        }
        if(phone.text.toString().isEmpty()) {
            phone.setError("Campul Telefon trebuie completat")
            return false
        }
        else{
            if(!validator.isPhoneNumber(phone.text.toString())){
                phone.setError("Introduceți un număr de telefon valid")
            return false
            }
        }
        if(password.text.toString().isEmpty()) {
            password.setError("Campul Parola trebuie completat")
            return false
        }
        if(password.text.toString().length<6){
            password.setError("Parola trebuie sa conțină cel putin 6 caractere")
            return false
        }
        return true
        }
    }
