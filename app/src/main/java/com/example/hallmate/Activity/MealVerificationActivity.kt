package com.example.hallmate.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.StaffAdapter
import com.example.hallmate.Adapter.TempAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Hall
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMealVerificationBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MealVerificationActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var scannedResultTextView: TextView
    private lateinit var switchCameraButton: Button
    private var useFrontCamera = false
    private val REQUEST_CAMERA_PERMISSION = 100
    lateinit var load : Loading
    var flagPass = false
    var database = FirebaseDatabase.getInstance()
    private lateinit var binding: ActivityMealVerificationBinding


    private lateinit var tempAdapter: TempAdapter
    private lateinit var list: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Init()

        binding.verify.setOnClickListener{
            checkForVerificationCode()
        }

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














        switchCameraButton.setOnClickListener {
            bal()
            useFrontCamera = !useFrontCamera
            restartScanner()
        }
    }

    fun bal(){
        val updatedData = tempAdapter.getPeriod()

        Toast.makeText(this,updatedData[0],Toast.LENGTH_SHORT).show()
    }

    private fun Init() {
        load = Loading(this)
        binding.second.visibility = View.VISIBLE
        binding.main.visibility = View.GONE
        barcodeView = findViewById(R.id.barcodeScannerView)
        scannedResultTextView = findViewById(R.id.scannedResultTextView)
        switchCameraButton = findViewById(R.id.switchCameraButton)


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        list = ArrayList()
        tempAdapter = TempAdapter(this,list)
        binding.recyclerView.adapter = tempAdapter




        // Check if the app has camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            startScanner()  // Start scanner if permission is already granted
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
              // scannedResultTextView.text = "Scanned Number: ${it.text}"

                verifyStudent(it.text)


            }
        }
    }

    private fun verifyStudent(text: String?) {
        // Ensure text is not null and has at least 5 characters (4 for hallId + 1 for key)
        if (text != null && text.length >= 5) {
            val hallId = text.substring(0, 4)  // First 4 characters are hallId
            val key = text.substring(4)         // Remaining part is the key


            scannedResultTextView.text = hallId + "\n" + key + "\n" + text

            // Assume you have a reference to Firebase or your data source
            val studentRef = FirebaseDatabase.getInstance().getReference("Student")

            Log.d("Firebase",hallId)
            // Search for the student by hallId
            studentRef.child(hallId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Student found, now check if the key matches
                            val student = snapshot.getValue(Student::class.java)
                        if (student!=null){

                            Log.d("Firebase", student.hallId.toString())
                            Log.d("Firebase",key)
                            Log.d("Firebase",student.key.toString())
                            if (student.key == key) {


                                // Success: Key matches
                                Toast.makeText(this@MealVerificationActivity, "Success: Student verified!", Toast.LENGTH_SHORT).show()
                            } else {
                                // Error: Key does not match
                                Toast.makeText(this@MealVerificationActivity, "Error: Invalid key!", Toast.LENGTH_SHORT).show()
                            }


                        }

                    } else {
                        // Error: Student not found
                        Toast.makeText(this@MealVerificationActivity, "Error: Student not found!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@MealVerificationActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Error: Invalid input
            Toast.makeText(this@MealVerificationActivity, "Error: Invalid input text!", Toast.LENGTH_SHORT).show()
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

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start the scanner
                    startScanner()
                } else {
                    // Permission denied, show a message
                    Toast.makeText(this, "Camera permission is required for scanning", Toast.LENGTH_SHORT).show()
                    Log.e("MealVerificationActivity", "Camera permission denied")
                }
            }
        }
    }

    private fun checkForVerificationCode() {
        val (day, month) = getCurrentDateAndMonth()
        val currentTime = getCurrentTime()
        load.start()
        var myCode = binding.code.text.toString()

        if(myCode.isEmpty()){
            load.end()
            Toast.makeText(this@MealVerificationActivity,"Enter code", Toast.LENGTH_SHORT).show()
            return
        }

        var hallRef = database.getReference("Hall")
        var dayMealStatusRef = database.getReference("DayMealStatus")

        hallRef
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var hall = snapshot.getValue(Hall::class.java)
                        if(hall!!.verificationCode==myCode){

                            dayMealStatusRef.child(month).child(day)
                                .addListenerForSingleValueEvent(object: ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)
                                            if(dayMealStatus!=null){

                                                var (flag,message) =   checkForTime(hall,dayMealStatus,currentTime)
                                                if(!flag){
                                                    load.end()
                                                    Toast.makeText(this@MealVerificationActivity,message, Toast.LENGTH_SHORT).show()
                                                }else{

                                                   list.add(message)
                                                    tempAdapter.notifyDataSetChanged()
                                                    binding.second.visibility = View.GONE
                                                    binding.main.visibility = View.VISIBLE
                                                    load.end()


                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })
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

    private fun checkForTime(hall: Hall, dayMealStatus: DayMealStatus, currentTime: String): Pair<Boolean,String> {

        if(isTimeInRange(hall.sahariStartTime.toString(),hall.sahariFinishTime.toString(),currentTime)){
            if(dayMealStatus.isRamadan==true){
                return Pair(true, "Lunch")
            }else{
             return Pair(false,"It's not meal time yet.")
            }
        }else if(isTimeInRange(hall.breakfastStartTime.toString(),hall.breakfastFinishTime.toString(),currentTime)){
            if(dayMealStatus.isRamadan==true){
                return Pair(false,"It's not meal time yet.")
            }else{
                return Pair(true, "BreakFast")
            }
        }
        else if(isTimeInRange(hall.lunchStartTime.toString(),hall.lunchFinishTime.toString(),currentTime)){
                return Pair(true, "Lunch")
        }
        else if(isTimeInRange(hall.iftarStartTime.toString(),hall.iftarFinishTime.toString(),currentTime)){
            if(dayMealStatus.isRamadan==true){
                return Pair(true, "BreakFast")
            }else{
                return Pair(false,"It's not meal time yet.")
            }
        }
        else if(isTimeInRange(hall.dinnerStartTime.toString(),hall.dinnerFinishTime.toString(),currentTime)){
            return Pair(true, "Dinner")
        }else{
            return Pair(false,"It's not meal time yet.")
        }

    }


    @SuppressLint("NewApi")
    fun isTimeInRange(time1: String, time2: String, time3: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        // Convert string times to LocalTime
        val localTime1 = LocalTime.parse(time1, formatter)
        var localTime2 = LocalTime.parse(time2, formatter)
        val localTime3 = LocalTime.parse(time3, formatter)

        // Add 15 minutes to the second time
        localTime2 = localTime2.plusMinutes(15)

        // Check if third time is between first and updated second time
        return  ((localTime3.isAfter(localTime1) ||localTime3.equals(localTime1) ) &&
                (localTime3.isBefore(localTime2) || localTime3.equals(localTime2)))
    }







    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()

        // Format Time as "21:35"
        return currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    fun getCurrentDateAndMonth(): Pair<String, String> {
        val calendar = Calendar.getInstance()

        // Format Date as "02"
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        // Format Month & Year as "12-2025"
        val monthYearFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val month = monthYearFormat.format(calendar.time)

        return Pair(date, month)
    }



}
