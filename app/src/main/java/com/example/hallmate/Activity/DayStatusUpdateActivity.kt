package com.example.hallmate.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.HallIdEmail
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityDayMealStatusBinding
import com.example.hallmate.databinding.ActivityDayStatusUpdateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DayStatusUpdateActivity : AppCompatActivity() {
    lateinit var binding : ActivityDayStatusUpdateBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()

    lateinit var month: String
    lateinit var day: String

    lateinit var load : Loading
    lateinit var load2:Loading2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDayStatusUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        load = Loading(this)
        load2 = Loading2(this)

        month= intent.getStringExtra("month").toString()
        day= intent.getStringExtra("day").toString()

        getAndSetData()
        checkTime()

        binding.back.setOnClickListener{
            finish()
        }


        binding.save.setOnClickListener{
            saveData()
        }

        binding.bStatus.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) { // Switch 1 OFF hole baki 2 OFF hobe
                binding.bMuttonOrBeef.isChecked = false
                binding.bAutoMeal.isChecked = false
                binding.bMuttonOrBeef.isEnabled = false
                binding.bAutoMeal.isEnabled = false
            } else { // Switch 1 ON hole baki 2 abar on kora jabe
                binding.bMuttonOrBeef.isEnabled = true
                binding.bAutoMeal.isEnabled = true
            }
        }

        binding.lStatus.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) { // Switch 1 OFF hole baki 2 OFF hobe
                binding.lMuttonOrBeef.isChecked = false
                binding.lAutoMeal.isChecked = false
                binding.lMuttonOrBeef.isEnabled = false
                binding.lAutoMeal.isEnabled = false
            } else { // Switch 1 ON hole baki 2 abar on kora jabe
                binding.lMuttonOrBeef.isEnabled = true
                binding.lAutoMeal.isEnabled = true
            }
        }


        binding.dStatus.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) { // Switch 1 OFF hole baki 2 OFF hobe
                binding.dMuttonOrBeef.isChecked = false
                binding.dAutoMeal.isChecked = false
                binding.dMuttonOrBeef.isEnabled = false
                binding.dAutoMeal.isEnabled = false
            } else { // Switch 1 ON hole baki 2 abar on kora jabe
                binding.dMuttonOrBeef.isEnabled = true
                binding.dAutoMeal.isEnabled = true
            }
        }

















    }


    private fun saveData() {

        load.start()

        val database = FirebaseDatabase.getInstance()
        val dateReference = database.getReference("DayMealStatus").child(month).child(day)




        val updateData: Map<String, Any?> = mapOf(

            "isRamadan" to binding.ramadan.isChecked,

            "bstatus" to binding.bStatus.isChecked,
            "bisMuttonOrBeaf" to binding.bMuttonOrBeef.isChecked,
            "bisAutoMeal" to binding.bAutoMeal.isChecked,
            "bmealCost" to (binding.bMealCost.text.toString().toDoubleOrNull()?: 0.0),
            "bfuelCost" to (binding.bFuelCost.text.toString().toDoubleOrNull()?: 0.0),
            "bextraMuttonCost" to (binding.bExtraMuttonCost.text.toString().toDoubleOrNull()?: 0.0),

            "lstatus" to binding.lStatus.isChecked,
            "lisMuttonOrBeaf" to binding.lMuttonOrBeef.isChecked,
            "lisAutoMeal" to binding.lAutoMeal.isChecked,
            "lmealCost" to (binding.lMealCost.text.toString().toDoubleOrNull()?: 0.0),
            "lfuelCost" to (binding.lFuelCost.text.toString().toDoubleOrNull()?: 0.0),
            "lextraMuttonCost" to (binding.lExtraMuttonCost.text.toString().toDoubleOrNull()?: 0.0),


            "dstatus" to binding.dStatus.isChecked,
            "disMuttonOrBeaf" to  binding.dMuttonOrBeef.isChecked,
            "disAutoMeal" to binding.dAutoMeal.isChecked,
            "dmealCost" to (binding.dMealCost.text.toString().toDoubleOrNull()?: 0.0),
            "dfuelCost" to (binding.dFuelCost.text.toString().toDoubleOrNull()?: 0.0),
            "dextraMuttonCost" to (binding.dExtraMuttonCost.text.toString().toDoubleOrNull()?: 0.0),

        )



        dateReference.updateChildren(updateData).addOnSuccessListener {

            checkForMealUpdate()


        }.addOnFailureListener { error ->
            load.end()
            Toast.makeText(this@DayStatusUpdateActivity,"Update\", \"Error: ${error.message}",Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkForMealUpdate() {

        var bl :Boolean  = false
        val updateData: MutableMap<String, Any?> = mutableMapOf()

        if(binding.bAutoMeal2.isChecked == false && binding.bAutoMeal.isChecked==true){
            updateData["bstatus"] = true
            bl = true
        }

        if(binding.lAutoMeal2.isChecked == false && binding.lAutoMeal.isChecked==true){
            updateData["lstatus"] = true
            bl = true
        }

        if(binding.dAutoMeal2.isChecked == false && binding.dAutoMeal.isChecked==true){
            updateData["dstatus"] = true
            bl = true
        }

        if(binding.bStatus2.isChecked==true && binding.bStatus.isChecked==false){
            updateData["bstatus"] = false
            bl = true
        }
        if(binding.lStatus2.isChecked==true && binding.lStatus.isChecked==false){
            updateData["lstatus"] = false
            bl = true
        }
        if(binding.dStatus2.isChecked==true && binding.dStatus.isChecked==false){
            updateData["dstatus"] = false
            bl = true
        }
        if(bl){

            var str: String = binding.txtDate.text.toString()
            str = str.replace("Date: ","")

            val day = str.substring(0, 2) // প্রথম ২ অক্ষর
            val month = str.substring(str.length - 7) // শেষ ৭ অক্ষর




            val fixedHallIds = mutableListOf<String>()


            val hallIdEmailRef = FirebaseDatabase.getInstance().getReference("HallIdEmail")


            hallIdEmailRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val hallIdEmail = childSnapshot.getValue(HallIdEmail::class.java)
                            hallIdEmail?.hallId?.let {
                                fixedHallIds.add(it)  // Add hallId to the list
                            }
                        }

                        for (hallId in fixedHallIds) {
                            val mealRef = FirebaseDatabase.getInstance().getReference("Meal")
                                .child(month)
                                .child(hallId)
                                .child(day)
                            mealRef.updateChildren(updateData)
                                .addOnSuccessListener {

                                    load.end()
                                    finish()


                                }
                                .addOnFailureListener { exception ->
                                    Log.e("UpdateError", "Failed to update data for HallId: $hallId", exception)
                                }
                        }
                        Toast.makeText(this@DayStatusUpdateActivity,"Updated Successfully",Toast.LENGTH_SHORT).show()

                    }else{
                        load.end()
                        finish()
                        Toast.makeText(this@DayStatusUpdateActivity,"Updated Successfully",Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    load.end()
                    Toast.makeText(this@DayStatusUpdateActivity,"Error: ${error.message}",Toast.LENGTH_SHORT).show()
                }
            })






        }else{

            load.end()
            finish()
            Toast.makeText(this@DayStatusUpdateActivity,"Updated Successfully",Toast.LENGTH_SHORT).show()


        }
    }

    private fun checkTime() {
       if(!shouldProceed(day+"-"+month)){
           binding.ramadan.isClickable = false

           binding.bStatus.isClickable = false
           binding.bMuttonOrBeef.isClickable = false
           binding.bAutoMeal.isClickable = false

           binding.lStatus.isClickable = false
           binding.lMuttonOrBeef.isClickable = false
           binding.lAutoMeal.isClickable = false

           binding.dStatus.isClickable = false
           binding.dMuttonOrBeef.isClickable = false
           binding.dAutoMeal.isClickable = false

       }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }



    fun shouldProceed(otherDate: String): Boolean {
        val currentDate = getCurrentDate()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // String কে Date অবজেক্টে রূপান্তর
        val current = dateFormat.parse(currentDate)
        val other = dateFormat.parse(otherDate)

        // যদি otherDate, currentDate এর সমান বা আগের হয়, তাহলে false
        if (other == current || other.before(current)) {
            return false
        }

        // Calendar সেট করা
        val calendar = Calendar.getInstance()

        // currentDate এর পরের দিন বের করা
        calendar.time = current
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val nextDate = calendar.time

        // যদি otherDate ঠিক currentDate এর পরের দিন হয়
        if (other == nextDate) {
            // এখন সময় কত তা বের করা
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

            // যদি এখন 8:00 PM (20:00) এর আগে হয়, তাহলে false, নাহলে true
            return currentHour < 20
        }

        // যদি otherDate currentDate এর পরের পরের দিন বা তার বেশি হয়, তাহলে true
        return true
    }



    private fun getAndSetData() {
        load2.start()
        val monthReference = database.getReference("DayMealStatus").child(month).child(day)
        monthReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)
                if(dayMealStatus!=null){
                    binding.txtDate.text = "Date: ${dayMealStatus.date}-${dayMealStatus.month}"
                    binding.ramadan.isChecked = dayMealStatus.isRamadan!!


                    binding.bStatus.isChecked = dayMealStatus.bstatus!!
                    binding.bMuttonOrBeef.isChecked = dayMealStatus.bisMuttonOrBeaf!!
                    binding.bAutoMeal.isChecked = dayMealStatus.bisAutoMeal!!
                    binding.bMealCost.setText(dayMealStatus.bmealCost?.toString() ?: "")
                    binding.bFuelCost.setText(dayMealStatus.bfuelCost?.toString() ?: "")
                    binding.bExtraMuttonCost.setText(dayMealStatus.bextraMuttonCost?.toString() ?: "")

                    binding.bStatus2.isChecked = dayMealStatus.bstatus!!
                    binding.bAutoMeal2.isChecked = dayMealStatus.bisAutoMeal!!


                    binding.lStatus.isChecked = dayMealStatus.lstatus!!
                    binding.lMuttonOrBeef.isChecked = dayMealStatus.lisMuttonOrBeaf!!
                    binding.lAutoMeal.isChecked = dayMealStatus.lisAutoMeal!!
                    binding.lMealCost.setText(dayMealStatus.lmealCost?.toString() ?: "")
                    binding.lFuelCost.setText(dayMealStatus.lfuelCost?.toString() ?: "")
                    binding.lExtraMuttonCost.setText(dayMealStatus.lextraMuttonCost?.toString() ?: "")

                    binding.lStatus2.isChecked = dayMealStatus.lstatus!!
                    binding.lAutoMeal2.isChecked = dayMealStatus.lisAutoMeal!!


                    binding.dStatus.isChecked = dayMealStatus.dstatus!!
                    binding.dMuttonOrBeef.isChecked = dayMealStatus.disMuttonOrBeaf!!
                    binding.dAutoMeal.isChecked = dayMealStatus.disAutoMeal!!
                    binding.dMealCost.setText(dayMealStatus.dmealCost?.toString() ?: "")
                    binding.dFuelCost.setText(dayMealStatus.dfuelCost?.toString() ?: "")
                    binding.dExtraMuttonCost.setText(dayMealStatus.dextraMuttonCost?.toString() ?: "")


                    binding.dStatus2.isChecked = dayMealStatus.dstatus!!
                    binding.dAutoMeal2.isChecked = dayMealStatus.disAutoMeal!!

                }

                load2.end()

            }

            override fun onCancelled(error: DatabaseError) {
                load2.end()
                Toast.makeText(this@DayStatusUpdateActivity,"Error: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }


}