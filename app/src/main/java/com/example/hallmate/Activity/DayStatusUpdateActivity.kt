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
import com.example.hallmate.Model.DayMealStatusForRecycler
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







    }


    private fun saveData() {

        load.start()

        val database = FirebaseDatabase.getInstance()
        val dateReference = database.getReference("DayMealStatus").child(month).child(day)




// আপডেট করা ডেটা তৈরি করছি
        val updateData = mapOf(
            "BreakFast" to DayMealStatus(
                status = binding.bStatus.isChecked,
                isMuttonOrBeaf =  binding.bMuttonOrBeef.isChecked ,
                isAutoMeal =  binding.bAutoMeal.isChecked,
                mealCost =  binding.bMealCost.text.toString().toDoubleOrNull() ?: 0.0,
                fuelCost = binding.bFuelCost.text.toString().toDoubleOrNull() ?: 0.0,
                extraMuttonCost = binding.bExtraMuttonCost.text.toString().toDoubleOrNull() ?: 0.0,
            ),
            "Lunch" to DayMealStatus(
                status = binding.lStatus.isChecked,
                isMuttonOrBeaf =  binding.lMuttonOrBeef.isChecked ,
                isAutoMeal =  binding.lAutoMeal.isChecked,
                mealCost =  binding.lMealCost.text.toString().toDoubleOrNull() ?: 0.0,
                fuelCost = binding.lFuelCost.text.toString().toDoubleOrNull() ?: 0.0,
                extraMuttonCost = binding.lExtraMuttonCost.text.toString().toDoubleOrNull() ?: 0.0,
            ),
            "Dinner" to DayMealStatus(
                status = binding.dStatus.isChecked,
                isMuttonOrBeaf =  binding.dMuttonOrBeef.isChecked ,
                isAutoMeal =  binding.dAutoMeal.isChecked,
                mealCost =  binding.dMealCost.text.toString().toDoubleOrNull() ?: 0.0,
                fuelCost = binding.dFuelCost.text.toString().toDoubleOrNull() ?: 0.0,
                extraMuttonCost = binding.dExtraMuttonCost.text.toString().toDoubleOrNull() ?: 0.0,
            ),
            "isRamadan" to binding.ramadan.isChecked
        )


        dateReference.updateChildren(updateData).addOnSuccessListener {

            load.end()
            finish()
            Toast.makeText(this@DayStatusUpdateActivity,"Updated Successfully",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { error ->
            load.end()
            Toast.makeText(this@DayStatusUpdateActivity,"Update\", \"Error: ${error.message}",Toast.LENGTH_SHORT).show()
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
                val isRamadan = snapshot.child("isRamadan").getValue(Boolean::class.java) ?: false
                Log.d("FirebaseData", "isRamadan: $isRamadan")

                val breakFast = snapshot.child("BreakFast").getValue(DayMealStatus::class.java)
                val lunch = snapshot.child("Lunch").getValue(DayMealStatus::class.java)
                val dinner = snapshot.child("Dinner").getValue(DayMealStatus::class.java)

                binding.txtDate.text = "Date: $day-$month"
                binding.ramadan.isChecked = isRamadan

                if (breakFast != null) {
                    Log.d("FirebaseData", "Breakfast: $breakFast")
                    binding.bStatus.isChecked = breakFast.status!!
                    binding.bMuttonOrBeef.isChecked = breakFast.isMuttonOrBeaf!!
                    binding.bAutoMeal.isChecked = breakFast.isAutoMeal!!
                    binding.bMealCost.setText(breakFast.mealCost?.toString() ?: "")
                    binding.bFuelCost.setText(breakFast.fuelCost?.toString() ?: "")
                    binding.bExtraMuttonCost.setText(breakFast.extraMuttonCost?.toString() ?: "")
                }

                if (lunch != null) {
                    Log.d("FirebaseData", "Lunch: $lunch")
                    binding.lStatus.isChecked = lunch.status!!
                    binding.lMuttonOrBeef.isChecked = lunch.isMuttonOrBeaf!!
                    binding.lAutoMeal.isChecked = lunch.isAutoMeal!!
                    binding.lMealCost.setText(lunch.mealCost?.toString() ?: "")
                    binding.lFuelCost.setText(lunch.fuelCost?.toString() ?: "")
                    binding.lExtraMuttonCost.setText(lunch.extraMuttonCost?.toString() ?: "")
                }

                if (dinner != null) {
                    Log.d("FirebaseData", "Dinner: $dinner")
                    binding.dStatus.isChecked = dinner.status!!
                    binding.dMuttonOrBeef.isChecked = dinner.isMuttonOrBeaf!!
                    binding.dAutoMeal.isChecked = dinner.isAutoMeal!!
                    binding.dMealCost.setText(dinner.mealCost?.toString() ?: "")
                    binding.dFuelCost.setText(dinner.fuelCost?.toString() ?: "")
                    binding.dExtraMuttonCost.setText(dinner.extraMuttonCost?.toString() ?: "")
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