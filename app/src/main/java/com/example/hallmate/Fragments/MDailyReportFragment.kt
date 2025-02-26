package com.example.hallmate.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hallmate.Adapter.StaffAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DailyReport
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMDailyReportBinding
import com.example.hallmate.databinding.FragmentMStaffBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MDailyReportFragment : Fragment() {
    private lateinit var binding: FragmentMDailyReportBinding

    private  var database = FirebaseDatabase.getInstance()
    private lateinit var load: Loading


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMDailyReportBinding.inflate(inflater, container, false)
        load = Loading(requireContext())















        return binding.root
    }



    private fun setAllMuttonStatus(day: String, month: String) {
        val muttonRef = database.getReference("Mutton")
        val mealRef = database.getReference("Meal")
        val dayStatusRef = database.getReference("DayMealStatus").child(month).child(day)

        dayStatusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)

                    if (dayMealStatus != null) {
                        val updates = mutableMapOf<String, Any>()

                        // Check conditions and update the map accordingly
                        if (dayMealStatus.bisMuttonOrBeaf == true) {
                            updates["bisMutton"] = true
                        }
                        if (dayMealStatus.lisMuttonOrBeaf == true) {
                            updates["lisMutton"] = true
                        }
                        if (dayMealStatus.disMuttonOrBeaf == true) {
                            updates["disMutton"] = true
                        }

                        if (updates.isNotEmpty()) {
                            muttonRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val batchUpdates = mutableMapOf<String, Any>()

                                    for (child in snapshot.children) {
                                        val hallId = child.key
                                        if (hallId != null) {
                                            val mealPath = "$month/$hallId/$day"
                                            updates.forEach { (key, value) ->
                                                batchUpdates["$mealPath/$key"] = value
                                            }
                                        }
                                    }

                                    if (batchUpdates.isNotEmpty()) {
                                        mealRef.updateChildren(batchUpdates)
                                            .addOnSuccessListener {
                                                Log.d("Firebase", "Mutton status updated successfully")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("FirebaseError", "Error updating meal data", e)
                                            }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("FirebaseError", "Error fetching mutton data: ${error.message}")
                                }
                            })
                        }else{

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching day meal status: ${error.message}")
            }
        })
    }


//    private fun generateReport(day: String, month: String) {
//        val hallIdEmailRef = database.getReference("HallIdEmail")
//        val mealRef = database.getReference("Meal")
//        val reportRef = database.getReference("DailyReport").child(month).child(day)
//        val dayStatusRef = database.getReference("DayMealStatus").child(month).child(day)
//
//        dayStatusRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)
//                    if (dayMealStatus != null) {
//
//                        hallIdEmailRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                if (snapshot.exists()) {
//                                    // Initialize Counters
//                                    var bStudentMeal = 0
//                                    var bMuttonMeal = 0
//
//                                    var lStudentMeal = 0
//                                    var lMuttonMeal = 0
//                                    var sahariMeal = 0
//
//                                    var dStudentMeal = 0
//                                    var dMuttonMeal = 0
//
//
//
//                                    var processedHalls = 0
//                                    val totalHalls = snapshot.childrenCount.toInt()
//
//                                    for (child in snapshot.children) {
//                                        val hallId = child.key
//
//                                        if (hallId != null) {
//                                            mealRef.child(month).child(hallId).child(day)
//                                                .addListenerForSingleValueEvent(object : ValueEventListener {
//                                                    override fun onDataChange(snapshot: DataSnapshot) {
//                                                        if (snapshot.exists()) {
//                                                            val meal = snapshot.getValue(Meal::class.java)
//
//                                                            if (meal != null) {
//                                                                // Increment counters based on meal status
//                                                                if (meal.bstatus == true) {
//                                                                    bStudentMeal++
//                                                                    if (meal.bisMutton == true) bMuttonMeal++
//                                                                }
//                                                                if (meal.lstatus == true) {
//                                                                    lStudentMeal++
//                                                                    if (meal.lisMutton == true) lMuttonMeal++
//                                                                    if (meal.isSahari == true) sahariMeal++
//                                                                }
//                                                                if (meal.dstatus == true) {
//                                                                    dStudentMeal++
//                                                                    if (meal.disMutton == true) dMuttonMeal++
//                                                                }
//
//                                                            }
//                                                        }
//
//                                                        // Track processed halls and update Firebase when all are processed
//                                                        processedHalls++
//                                                        if (processedHalls == totalHalls) {
//                                                            val report = DailyReport(
//                                                                month = month,
//                                                                date = day,
//                                                                isRamadan = dayMealStatus.isRamadan, // Using dayStatusRef Data
//                                                                bStudentMeal = bStudentMeal,
//                                                                bStaffMeal = bStaffMeal,
//                                                                bGuestMeal = bGuestMeal,
//                                                                bMuttonMeal = bMuttonMeal,
//                                                                lStudentMeal = lStudentMeal,
//                                                                lStaffMeal = lStaffMeal,
//                                                                lGuestMeal = lGuestMeal,
//                                                                lMuttonMeal = lMuttonMeal,
//                                                                SahariMeal = sahariMeal,
//                                                                dStudentMeal = dStudentMeal,
//                                                                dStaffMeal = dStaffMeal,
//                                                                dGuestMeal = dGuestMeal,
//                                                                dMuttonMeal = dMuttonMeal
//                                                            )
//
//                                                            // Save report to Firebase
//                                                            reportRef.setValue(report)
//                                                                .addOnSuccessListener {
//                                                                    Log.d("Firebase", "Daily report generated successfully")
//                                                                }
//                                                                .addOnFailureListener { e ->
//                                                                    Log.e("FirebaseError", "Failed to save daily report: ${e.message}")
//                                                                }
//                                                        }
//                                                    }
//
//                                                    override fun onCancelled(error: DatabaseError) {
//                                                        Log.e("FirebaseError", "Error fetching meal data: ${error.message}")
//                                                    }
//                                                })
//                                        }
//                                    }
//
//                                    // Edge case: No halls exist
//                                    if (totalHalls == 0) {
//                                        reportRef.setValue(DailyReport(month, day, isRamadan = dayMealStatus.isRamadan))
//                                            .addOnSuccessListener {
//                                                Log.d("Firebase", "Daily report generated successfully with no data")
//                                            }
//                                            .addOnFailureListener { e ->
//                                                Log.e("FirebaseError", "Failed to save empty daily report: ${e.message}")
//                                            }
//                                    }
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                Log.e("FirebaseError", "Error fetching hall data: ${error.message}")
//                            }
//                        })
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseError", "Error fetching day meal status: ${error.message}")
//            }
//        })
//    }








}