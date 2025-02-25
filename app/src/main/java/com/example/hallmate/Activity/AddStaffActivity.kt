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
import com.example.hallmate.Model.Hall
import com.example.hallmate.Model.HallIdEmail
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityAddStaffBinding
import com.example.hallmate.databinding.ActivityDayStatusUpdateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class AddStaffActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddStaffBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()


    lateinit var load : Loading
    lateinit var load2: Loading2
     var hallId :String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        load = Loading(this)
        load2 = Loading2(this)


        binding.add.setOnClickListener{
            saveStaff()
        }

        binding.back.setOnClickListener{
            finish()
        }





    }



    private fun saveStaff() {
        load.start()
        val name = binding.name.text.toString()
        val position = binding.position.text.toString()


        if(name.isEmpty() || position.isEmpty()){
            load.end()
            Toast.makeText(this,"Enter name and position",Toast.LENGTH_SHORT).show()
            return
        }



        val studentRef = database.getReference("Student")
        val hallRef = database.getReference("Hall")
        val hallIdEmailRef = database.getReference("HallIdEmail")

        val key = UUID.randomUUID().toString()

        // Get the Hall data and update it
        hallRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val hall = snapshot.getValue(Hall::class.java)
                hall?.let {
                    // Get the lastHallId and increment it
                    hallId = hall.lastHallId.toString()
                    val id: Int = (hall.lastHallId?.toInt() ?: 0) + 1

                    val updates = hashMapOf<String, Any>(
                        "lastHallId" to id, // Update the lastHallId
                    )

                    // Perform the update
                    hallRef.updateChildren(updates)
                        .addOnSuccessListener {
                            //Log.d("Hall Update", "Successfully updated Hall")

                                    val student = Student(
                                        hallId, "", name, hallId+"@gmail.com", "", position, "1", "", false,
                                        0.0, key, "", "", "",false,false
                                    )

                                    val hallIdEmail = HallIdEmail(hallId,hallId+"@gmail.com")

                                    // Save the student data in the "Student" node
                                    studentRef.child(hallId).setValue(student)
                                        .addOnSuccessListener {
                                            // Create the user account in Firebase Authentication

                                                    hallIdEmailRef.child(hallId).setValue(hallIdEmail)
                                                        .addOnSuccessListener {

                                                            startJourney(hallId)

                                                        }
                                                        .addOnFailureListener {
                                                            load.end()
                                                            Toast.makeText(
                                                                this@AddStaffActivity, "Failed to add Staff.", Toast.LENGTH_SHORT).show()
                                                        }

                                        }
                                        .addOnFailureListener {
                                            load.end()
                                            Toast.makeText(
                                                this@AddStaffActivity, "Failed to add Staff.", Toast.LENGTH_SHORT).show()
                                        }

                        }
                        .addOnFailureListener { exception ->
                            // Log failure and show error message
                            load.end()
                            Toast.makeText(
                                this@AddStaffActivity, "Failed to add Staff.", Toast.LENGTH_SHORT).show()                        }
                }
            } else {
                // If no data is found in the Hall node
                load.end()
                Toast.makeText(
                    this@AddStaffActivity, "Failed to add Staff.", Toast.LENGTH_SHORT).show()            }
        }.addOnFailureListener {
            // Log error if fetching Hall data failed
            load.end()
            Toast.makeText(
                this@AddStaffActivity, "Failed to add Staff.", Toast.LENGTH_SHORT).show()        }
    }


    private fun startJourney(hallId: String) {
        val mealRef = FirebaseDatabase.getInstance().getReference("Meal")

        var feb = "02-2025"
        var mar = "03-2025"
        var apr = "04-2025"
        var may = "05-2025"

        //For february


        for(i in 1..4){
            var month:String = feb
            var limit :Int = 0
            when (i){
                1->{
                    month = feb
                    limit = 28
                }
                2->{
                    month = mar
                    limit = 31
                }
                3->{
                    month = apr
                    limit = 30
                }
                4->{
                    month = may
                    limit = 31
                }
            }


            for(d in 1..limit){
                var day = d.toString()
                if(d<10)
                    day = "0"+day


                var meal = Meal(month,day,hallId,false,
                    false,false,false,
                    false,false,false,
                    false,false,false)



                mealRef.child(month).child(hallId).child(day).setValue(meal)
                    .addOnSuccessListener {
                        if (i == 4 && d == 31) {
                            load.end()
                            Toast.makeText(
                                this@AddStaffActivity, "Successfully Added", Toast.LENGTH_SHORT).show()
                            finish()


                        }
                    }
                    .addOnFailureListener {
                        load.end()
                        Toast.makeText(
                            this@AddStaffActivity, "Failed to add Staff.", Toast.LENGTH_SHORT).show()                    }
            }


        }







    }




}