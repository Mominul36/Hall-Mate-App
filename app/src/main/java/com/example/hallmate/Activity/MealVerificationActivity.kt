package com.example.hallmate.Activity

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.example.hallmate.Class.Loading
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityManagementLoginBinding
import com.example.hallmate.databinding.ActivityMealVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

class MealVerificationActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var scannedResultTextView: TextView
    private lateinit var switchCameraButton: Button

    private lateinit var binding: ActivityMealVerificationBinding
    private lateinit var auth: FirebaseAuth
     var database = FirebaseDatabase.getInstance()

    lateinit var load : Loading
    var flagPass = false

    private var useFrontCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMealVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)
        auth = FirebaseAuth.getInstance()

        binding.verify.setOnClickListener{
            checkForVerificationCode()
        }

        binding.second.visibility = View.VISIBLE
        binding.main.visibility = View.GONE

        binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.passHiddenIcon.setOnClickListener {
            if (flagPass) {
                binding.code.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.code.transformationMethod = null
                binding.passHiddenIcon.setImageResource(R.drawable.show)
            }
            binding.code.setSelection(binding.code.text.length)
            flagPass = !flagPass
        }


        barcodeView = findViewById(R.id.barcodeScannerView)
        scannedResultTextView = findViewById(R.id.scannedResultTextView)
        switchCameraButton = findViewById(R.id.switchCameraButton)

        startScanner()

        // Switch Camera Button Click Listener
        switchCameraButton.setOnClickListener {
            useFrontCamera = !useFrontCamera  // Toggle camera selection
            restartScanner()  // Restart scanner with the new camera
        }


    }


    private fun startScanner() {
        barcodeView.barcodeView.cameraSettings.isBarcodeSceneModeEnabled = true
        barcodeView.barcodeView.cameraSettings.requestedCameraId = if (useFrontCamera) 1 else 0  // 0 = Back, 1 = Front
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(listOf(com.google.zxing.BarcodeFormat.QR_CODE))
        barcodeView.decodeContinuous(callback)  // Start scanning
        barcodeView.resume()
    }

    private fun restartScanner() {
        barcodeView.pause()  // Stop the current scanner
        startScanner()  // Restart with the new camera
    }


    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let {
                scannedResultTextView.text = "Scanned Number: ${it.text}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    private fun checkForVerificationCode() {
        load.start()
        var myCode = binding.code.text.toString()

        if(myCode.isEmpty()){
            load.end()
            Toast.makeText(this@MealVerificationActivity,"Enter code", Toast.LENGTH_SHORT).show()
           return
        }

        var hallRef = database.getReference("Hall")

        hallRef.child("verificationCode")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var code = snapshot.getValue(String::class.java)
                         if(code==myCode){
                             binding.second.visibility = View.GONE
                             binding.main.visibility = View.VISIBLE
                             load.end()

                         }else{
                             load.end()
                             Toast.makeText(this@MealVerificationActivity,"Incorrect Verification Code", Toast.LENGTH_SHORT).show()
                         }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    load.end()
                    Toast.makeText(this@MealVerificationActivity,"Error: ${error.message}", Toast.LENGTH_SHORT).show()

                }

            })


    }
}