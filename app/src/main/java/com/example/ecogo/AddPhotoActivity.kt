package com.example.ecogo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var chooseButton:Button
    private lateinit var uploadButton:Button
    private var storageReference:StorageReference=FirebaseStorage.getInstance().getReference()
    private var selectedImage:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        getSupportActionBar()?.setTitle("Adauga poza")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        chooseButton=findViewById(R.id.choosePhoto)
        uploadButton=findViewById(R.id.uploadPhoto)
        chooseButton.setOnClickListener(){
            var intent= Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent,3)
        }
        uploadButton.setOnClickListener(){
            if(selectedImage!=null)
          addPhoto()
            else
                Toast.makeText( baseContext, "Selectati mai intai o poza", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK && data!=null && requestCode==3){
            selectedImage =data.getData()
            var image=findViewById<ImageView>(R.id.imageView2)
            image.setImageURI(selectedImage)
        }
    }
    private fun addPhoto(){
        var name:String=UUID.randomUUID().toString()
        storageReference.child(name).putFile(selectedImage!!).addOnSuccessListener {
            storageReference.child(name).downloadUrl.addOnSuccessListener {
                var currentUserId=FirebaseAuth.getInstance().getCurrentUser()?.uid
                Firebase.database.getReference("Users").child(currentUserId.toString()).child("photoLink").setValue(it.toString()).addOnSuccessListener {
                    Toast.makeText( baseContext, "Poza a fost incarcata cu succes", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText( baseContext, "Eroare incarcare poza", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}