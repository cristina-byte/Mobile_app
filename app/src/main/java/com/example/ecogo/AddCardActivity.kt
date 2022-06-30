package com.example.ecogo

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddCardActivity : AppCompatActivity() {
    private lateinit var ownerName: EditText
    private lateinit var code:EditText
    private lateinit var cvv:EditText
    private lateinit var expiringDate:EditText
    private var reference=Firebase.database.getReference("Cards")
    private lateinit var addButton:Button
    private var currentUserId=FirebaseAuth.getInstance().getCurrentUser()?.uid
    private var cards=ArrayList<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_card)
        ownerName=findViewById(R.id.editTextName2)
        code =findViewById(R.id.editTextName3)
        cvv=findViewById(R.id.editTextName4)
        expiringDate=findViewById(R.id.editTextName5)
        addButton=findViewById<Button>(R.id.setCard)
        addButton.setOnClickListener(){
            if(validate()){
                addCard()
            }
        }
        val calendar=Calendar.getInstance()
        val datePicker=DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            calendar.set(Calendar.YEAR,i)
            calendar.set(Calendar.MONTH,i2)
            calendar.set(Calendar.DAY_OF_MONTH,i3)
            update(calendar)
        }
        expiringDate.setOnClickListener{
            DatePickerDialog(this,datePicker,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun calendarToString(calendar:Calendar):String{
        return calendar.get(Calendar.YEAR).toString()+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.DAY_OF_MONTH)

    }
    private fun update(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1)
        expiringDate.setText(calendarToString(calendar))
    }
    private fun validate():Boolean{
        expiringDate.setError(null)
        if(ownerName.text.toString().length==0){
            ownerName.setError("Acest camp trebuie completat")
            return false
        }
        if(code.text.toString().length==0){
            code.setError("Acest camp trebuie completat")
            return false
        }
        if(code.text.toString().length!=16) {
            code.setError("Lungimea codului este greșită")
            return false
        }
        if(expiringDate.text.toString().length==0){
            expiringDate.setError("Acest camp trebuie completat")
            return false
        }
        if(Validator().isValidDate(expiringDate.text.toString())==false){
            expiringDate.setError("Data de expirare este incorectă")
            return false
        }
        if(cvv.text.toString().length==0){
            cvv.setError("Acest camp trebuie completat")
            return false
        }
        if(cvv.text.toString().length!=3){
            cvv.setError("Lungimea campului este greșită")
            return false
        }
        return true
    }


    private fun dateToLong(date: String): Long {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        return dateFormat.parse(date).time
    }


    private fun addCard(){
        reference.get().addOnSuccessListener {
            for( c in it.children){
                cards.add(c.getValue(Card::class.java)!!)
                cards[cards.size-1].id=c.key.toString()
            }
            var card=Card(ownerName.text.toString(), code.text.toString().toLong(), dateToLong(expiringDate.text.toString()),cvv.text.toString())
            var idCard=searchCard(card)
            if(idCard!=null){
                insertCard(idCard)
                var intent = Intent(this@AddCardActivity, AuthActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,"Datele cardului sunt incorecte",Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun searchCard(card:Card):String?{
        for(i in 0..cards.size-1){
            println(cards[i].expiringDate)
            if(card.equals(cards[i]))
                return cards[i].id
        }
        return null
    }


    private fun insertCard(idCard:String){
        Firebase.database.getReference("Users").child(currentUserId!!).child("Card").setValue(idCard)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Cardul a fost adaugat", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Eroare adaugare card", Toast.LENGTH_SHORT).show()
            }
    }


}


