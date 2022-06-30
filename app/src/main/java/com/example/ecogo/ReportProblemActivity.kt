package com.example.ecogo

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class ReportProblemActivity : AppCompatActivity() {
    private lateinit var reportButton:Button
    private lateinit var addPhoto:Button
    private lateinit var problemType:Spinner
    private lateinit var reportDate:EditText
    private lateinit var description:EditText
    private var storageReference:StorageReference=FirebaseStorage.getInstance().getReference()
    private val problemsReference = Firebase.database.getReference("Problems")
    private var photoLink: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        getSupportActionBar()?.setTitle("Raportează problema")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        val calendar=Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            calendar.set(Calendar.YEAR,i)
            calendar.set(Calendar.MONTH,i2)
            calendar.set(Calendar.DAY_OF_MONTH,i3)
            update(calendar)
        }
        reportDate=findViewById(R.id.dataRaportare)
        reportDate.setOnClickListener{
            DatePickerDialog(this,datePicker,
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        problemType=findViewById(R.id.tipProblema)
        description=findViewById(R.id.descriereProblema)
        problemType=findViewById(R.id.tipProblema)
        reportButton=findViewById(R.id.reportProblem)
        addPhoto=findViewById(R.id.addProblemImage)
        addPhoto.setOnClickListener(){
            var intent=Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent,1)
        }
        reportButton.setOnClickListener(){
           if(validate()){
               var problem=Problem(description.text.toString(), problemType.selectedItem.toString(),reportDate.text.toString(),it.toString())
               reportProblem(problem)
           }
        }
    }
   private fun validate():Boolean{
        reportDate.setError(null)
        if(description.text.toString().isEmpty()){
           description.setError("Campul descriere trebuie completat")
            return false
        }
        if(reportDate.text.toString().isEmpty()){
            reportDate.setError("Campul data trebuie completat")
            return false
        }
        if(Validator().isValidDate2(reportDate.text.toString())==false){
            reportDate.setError("Data raportarii nu este corecta")
            return false
        }

        if(photoLink==null){
            Toast.makeText(this,"Trebuie sa adaugati o poza",Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
    private fun calendarToString(calendar:Calendar):String{
        return calendar.get(Calendar.YEAR).toString()+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.DAY_OF_MONTH)
    }
    private fun update(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1)
        println("Calendar:"+calendar)
        reportDate.setText(calendarToString(calendar))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && data!=null){
            photoLink= data.getData()!!
            var img=findViewById<ImageView>(R.id.problemImage)
            img.setImageURI(photoLink)
        }
    }
    private fun reportProblem(problem:Problem){
        var name:String= UUID.randomUUID().toString()
        storageReference.child(name).putFile(photoLink!!).addOnSuccessListener {
            storageReference.child(name).downloadUrl.addOnSuccessListener {

               problemsReference.push().setValue(problem).addOnSuccessListener {
                   Toast.makeText(baseContext,"Problema a fost raportata", Toast.LENGTH_LONG).show()
               }.addOnFailureListener{
                   Toast.makeText(baseContext,"Eroare raportare problema", Toast.LENGTH_LONG).show()
               }

                
            }
        }
    }
}


