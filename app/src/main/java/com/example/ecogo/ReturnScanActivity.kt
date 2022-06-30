package com.example.ecogo

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

private const val CAMERA_REQUEST_CODE=101
class ReturnScanActivity : AppCompatActivity() {

    private lateinit var scannerQr: CodeScanner
    private lateinit var scannerQrView: CodeScannerView
    private lateinit var dialogOnSucces: Dialog
    private lateinit var dialogOnError: Dialog
    private lateinit var nextButton: Button
    private lateinit var retryButton: Button
    private var destinationSlotKey:String?=null
    private lateinit var reservation:Reservation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_scan)
        reservation=intent.getSerializableExtra("reservation") as Reservation
        dialogOnSucces=Dialog(this)
        dialogOnSucces.setContentView(R.layout.scan_dialog_on_succes)
        dialogOnSucces.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.background_dialog))
        dialogOnSucces.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        nextButton=dialogOnSucces.findViewById(R.id.finish2)

        var activitylauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:ActivityResultCallback<ActivityResult>{
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode==100)
                    setResult(100)
                dialogOnSucces.cancel()
                finish()
            }
        })

        nextButton.setOnClickListener{
            var currentDate= Calendar.getInstance()
            var startDate= Calendar.getInstance()
            startDate.setTime(reservation.startDate)
            var duration=(currentDate.timeInMillis-startDate.timeInMillis)/1000
            reservation.time=duration.toInt()
            var intent=Intent(this,FinishReservationActivity::class.java)
            intent.putExtra("keyRezervare",getIntent().getStringExtra("keyRezervare"))
            intent.putExtra("reservation",reservation)
            activitylauncher.launch(intent)

        }
        dialogOnError=Dialog(this)
        dialogOnError.setContentView(R.layout.scan_dialog_on_error)
        dialogOnError.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.background_dialog))
        dialogOnError.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        retryButton=dialogOnError.findViewById(R.id.finish2)
        retryButton.setOnClickListener{
            dialogOnError.cancel()
            scannerQr.startPreview()
        }
        scannerQrView= findViewById(R.id.scanner_view2)

        setPermissions()
        scanQr()
        scannerQrView.setOnClickListener{
            scannerQr.startPreview()
        }
    }
    private fun scanQr(){
        scannerQr = CodeScanner(this, scannerQrView)
        scannerQr.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback= DecodeCallback {
                runOnUiThread{
                    println("s-a executat scanarea din return class")
                    var slotCode=it.text.toString()
                    reservation.slotKeyDestination=slotCode
                    var destinationPointKey=reservation.destinationPoint
                    Firebase.database.getReference("Pick-upPoints").child(destinationPointKey).child("slots").
                    get().addOnSuccessListener {
                            destinationSlotKey=searchSlot(it,slotCode)
                            if(destinationSlotKey!=null){
                                checkStatus(destinationSlotKey.toString())
                            }
                            else {
                                if(!isFinishing())
                                dialogOnError.show()
                            }
                        }
                }
            }
            errorCallback= ErrorCallback{
                runOnUiThread{
                    Log.e("main","Camera initialization error:${it.message}")
                }
            }
        }
    }
    private fun checkStatus(slotKey:String){
        var pointKey=reservation.destinationPoint
        Firebase.database.getReference("Pick-upPoints").
        child(pointKey).child("slots").child(slotKey).get().addOnSuccessListener {
            if(it.child("type").getValue(String::class.java).equals(reservation.transportType)) {
                if (it.child("rented").getValue(Boolean::class.java) == false) {
                    Toast.makeText(this@ReturnScanActivity, "Statia nu este libera.", Toast.LENGTH_SHORT).show()

                } else {
                    scannerQr.stopPreview()
                    reservation.slotKeyDestination=slotKey
                    if(!isFinishing())
                    dialogOnSucces.show()
                }
            }
            else {
                Toast.makeText(
                    this@ReturnScanActivity,
                    "Tipul slotului nu corespunde cu tipul mijlocului de transport",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
    override fun onResume() {
        super.onResume()
        scannerQr.startPreview()
    }
    override fun onPause() {
        scannerQr.releaseResources()
        super.onPause()
    }
    private fun setPermissions(){
        val permission= ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if(permission!= PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }
    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if(grantResults.isEmpty() || grantResults[0]!= PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(baseContext,"You need camera permission to be able to use this app",
                        Toast.LENGTH_SHORT).show()
                else{}
            }
        }
    }
    private fun searchSlot(slots:DataSnapshot, slotCode:String):String?{
        for(slot in slots.children) {
            var s: Slot? = slot.getValue(Slot::class.java)
            if (slotCode.equals(s?.code.toString()))
                return slot.key
        }
        return null
    }
}