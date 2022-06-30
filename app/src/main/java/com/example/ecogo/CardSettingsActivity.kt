package com.example.ecogo

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class CardSettingsActivity : AppCompatActivity() {
    private lateinit var ownerName: EditText
    private lateinit var code:EditText
    private lateinit var expiringDate:EditText
    private lateinit var cvv:EditText
    private var reference=Firebase.database.getReference("Cards")
    private lateinit  var currentSold:TextView
    private lateinit var currentCode:TextView
    private lateinit var currentExpiringDate:TextView
    private lateinit  var saveButton:Button
    private lateinit var cancelButton:Button
    private var cards=ArrayList<Card>()
    private var currentUserId=FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        getData()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_settings)
        getSupportActionBar()?.setTitle("Setări Card")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        currentSold=findViewById(R.id.sold)
        currentCode=findViewById(R.id.cardNumber)
        currentExpiringDate=findViewById(R.id.expireDate)
        ownerName=findViewById(R.id.numeDetinator)
        code =findViewById(R.id.cod)
        expiringDate=findViewById(R.id.dataExpirare)
        cvv=findViewById(R.id.cvv)
        val calendar=Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            calendar.set(Calendar.YEAR,i)
            calendar.set(Calendar.MONTH,i2)
            calendar.set(Calendar.DAY_OF_MONTH,i3)
            update(calendar)
        }
        expiringDate.setOnClickListener{
            DatePickerDialog(this,datePicker,
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        saveButton=findViewById(R.id.save_card)
        cancelButton=findViewById(R.id.cancel_card)
        saveButton.setOnClickListener(){
            if(validate()) {
               addCard()
            }
        }
        cancelButton.setOnClickListener(){
            ownerName.setText("")
            code.setText("")
            cvv.setText("")
            expiringDate.setText("")
        }
    }

    private fun getData() {
        var reference= Firebase.database.getReference("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).
        child("Card")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var cardKey=snapshot.getValue(String::class.java)
                if(cardKey!=null){
                reference.child(cardKey).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        currentSold.setText(snapshot.child("balance").getValue(Double::class.java).toString()+" RON")
                        currentCode.setText(snapshot.child("code").getValue(Long::class.java).toString())
                        currentExpiringDate.setText(longToDate(snapshot.child("expiringDate").getValue(Long::class.java)!!))
                    }
                    override fun onCancelled(error: DatabaseError) {}






                })
                }else Toast.makeText(this@CardSettingsActivity,"Adaugati un card pentru a putea inchiria mijloace de transport",Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
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
            }
            else{
                Toast.makeText(this,"Datele cardului sunt incorecte",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun searchCard(card:Card):String?{
        for(i in 0..cards.size-1){
            if(card.equals(cards[i]))
                return cards[i].id
        }
        return null
    }
    private fun insertCard(idCard:String){
        Firebase.database.getReference("Users").child(currentUserId).child("Card").setValue(idCard)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Cardul a fost actualizat", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Eroare actualizare card", Toast.LENGTH_SHORT).show()
            }
    }
    private fun dateToLong(date: String): Long {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        return dateFormat.parse(date).time
    }
    private fun longToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }
private fun calendarToString(calendar:Calendar):String{
    return calendar.get(Calendar.YEAR).toString()+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.DAY_OF_MONTH)
}
private fun update(calendar: Calendar) {
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1)
    expiringDate.setText(calendarToString(calendar))
}
    private fun validate():Boolean {
        expiringDate.setError(null)

        if (ownerName.text.toString().length == 0) {
            ownerName.setError("Acest camp trebuie completat")
            return false
        }
        if (code.text.toString().length == 0) {
            code.setError("Acest camp trebuie completat")
            return false
        }
        if (code.text.toString().length != 16) {
            code.setError("Lungimea codului este greșită")
            return false
        }
        if (expiringDate.text.toString().length == 0) {
            expiringDate.setError("Acest camp trebuie completat")
            return false
        }
        if (Validator().isValidDate(expiringDate.text.toString()) == false) {
            expiringDate.setError("Data de expirare este incorectă")
            return false
        }
        if (cvv.text.toString().length == 0) {
            cvv.setError("Acest camp trebuie completat")
            return false
        }
        if (cvv.text.toString().length != 3) {
            cvv.setError("Lungimea campului este greșită")
            return false
        }
        return true
    }
}