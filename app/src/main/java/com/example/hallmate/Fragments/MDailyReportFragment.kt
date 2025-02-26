package com.example.hallmate.Fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.example.hallmate.Class.Loading
import com.example.hallmate.Model.DailyReport
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Meal
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMDailyReportBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MDailyReportFragment : Fragment() {
    private lateinit var binding: FragmentMDailyReportBinding
    private val calendar = Calendar.getInstance()
    private  var database = FirebaseDatabase.getInstance()
    lateinit var load :Loading

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMDailyReportBinding.inflate(inflater, container, false)
        load = Loading(requireContext())

        // Set OnClickListener for ImageView
        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }

        binding.card.visibility = View.GONE
        setdate()

        binding.check.setOnClickListener{
            checkForReport()
        }






        return binding.root
    }


    private fun checkForReport() {
        load.start()
        val date = binding.txtdate.text.toString()
        val day = date.take(2)
        val month = date.takeLast(7)


        val result = validateDate(date)
        if (!result.first) {
            load.end()
            Toast.makeText(requireContext(), result.second, Toast.LENGTH_SHORT).show()
            return
        }

        val dailyReportRef =  database.getReference("DailyReport")
        dailyReportRef.child(month).child(day)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var report = snapshot.getValue(DailyReport::class.java)
                        if(report!=null){
                            setReport(report)
                        }
                        else{
                            load.end()
                            showGuestDialog(day,month)
                        }
                    }else{
                        load.end()
                        showGuestDialog(day,month)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setReport(report: DailyReport) {
        var d = report.date+"-"+report.month
        binding.cardDate.setText(d)
        binding.cardDay.setText(getDayName(d))


        binding.bStudentMeal.setText(report.bStudentMeal.toString())
        binding.bGuestMeal.setText(report.bGuestMeal.toString())
        binding.bMuttonMeal.setText(report.bMuttonMeal.toString())
        binding.bTotalMeal.setText((report.bStudentMeal!!.toInt() + report.bGuestMeal!!.toInt()).toString())


        binding.lStudentMeal.setText(report.lStudentMeal.toString())
        binding.lGuestMeal.setText(report.lGuestMeal.toString())
        binding.lMuttonMeal.setText(report.lMuttonMeal.toString())
        binding.sahariMeal.setText(report.SahariMeal.toString())
        binding.lunchMeal.setText(((report.lStudentMeal!!.toInt() + report.lGuestMeal!!.toInt()) - report.SahariMeal!!.toInt()).toString())
        binding.lTotalMeal.setText((report.lStudentMeal!!.toInt() + report.lGuestMeal!!.toInt()).toString())


        binding.dStudentMeal.setText(report.dStudentMeal.toString())
        binding.dGuestMeal.setText(report.dGuestMeal.toString())
        binding.dMuttonMeal.setText(report.dMuttonMeal.toString())
        binding.dTotalMeal.setText((report.dStudentMeal!!.toInt() + report.dGuestMeal!!.toInt()).toString())


        binding.card.visibility = View.VISIBLE
        load.end()
    }


    private fun showGuestDialog(day:String, month:String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_for_guest_meal)

        val breakfast = dialog.findViewById<EditText>(R.id.breakfast)
        val lunch = dialog.findViewById<EditText>(R.id.lunch)
        val dinner = dialog.findViewById<EditText>(R.id.dinner)
        val set = dialog.findViewById<Button>(R.id.set)
        val cross = dialog.findViewById<ImageView>(R.id.cross)



        cross.setOnClickListener{
            dialog.dismiss()
        }

        set.setOnClickListener {
            var b = breakfast.text.toString()
            var l = lunch.text.toString()
            var d = dinner.text.toString()

            if(b.isEmpty()){
                b = "0"
            }
            if(l.isEmpty()){
                l = "0"
            }
            if(d.isEmpty()){
                d = "0"
            }

            dialog.dismiss()
            load.start()
            setAllMuttonStatus(day,month,b,l,d)

        }
        dialog.show()
    }

    private fun setAllMuttonStatus(day: String, month: String,bg:String,lg:String,dg:String) {
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

                                                generateReport(day,month,bg,lg,dg)
                                                load.end()
                                                Log.d("Firebase", "Mutton status updated successfully")
                                            }
                                            .addOnFailureListener { e ->
                                                load.end()
                                                Toast.makeText(requireContext(),"Error: ${e.message}",Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    load.end()
                                    Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()

                                }
                            })
                        }else{
                            Log.d("Firebase", "here")
                            generateReport(day,month,bg,lg,dg)

                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                load.end()
                Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun generateReport(day: String, month: String,bg:String,lg:String,dg:String) {
        val hallIdEmailRef = database.getReference("HallIdEmail")
        val mealRef = database.getReference("Meal")
        val reportRef = database.getReference("DailyReport")
        val dayStatusRef = database.getReference("DayMealStatus").child(month).child(day)

        dayStatusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)
                    if (dayMealStatus != null) {

                        hallIdEmailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    binding.BStudentMeal.setText("0")
                                    binding.BMuttonMeal.setText("0")
                                    binding.LStudentMeal.setText("0")
                                    binding.LMuttonMeal.setText("0")
                                    binding.SahariMeal.setText("0")
                                    binding.DStudentMeal.setText("0")
                                    binding.DMuttonMeal.setText("0")


                                    for (child in snapshot.children) {
                                        val hallId = child.key
                                        if (hallId != null) {
                                            mealRef.child(month).child(hallId).child(day)
                                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if (snapshot.exists()) {
                                                            val meal = snapshot.getValue(Meal::class.java)
                                                            if (meal != null) {
                                                                // Increment counters based on meal status
                                                                if (meal.bstatus == true) {
                                                                    var value = binding.BStudentMeal.text.toString().toInt()
                                                                    value = value+1
                                                                    Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                    binding.BStudentMeal.setText(value.toString())

                                                                    if (meal.bisMutton == true){
                                                                        var value = binding.BMuttonMeal.text.toString().toInt()
                                                                        value = value+1
                                                                        Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                        binding.BMuttonMeal.setText(value.toString())
                                                                    }
                                                                }
                                                                if (meal.lstatus == true) {
                                                                    var value = binding.LStudentMeal.text.toString().toInt()
                                                                    value = value+1
                                                                    Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                    binding.LStudentMeal.setText(value.toString())

                                                                    if (meal.lisMutton == true){
                                                                        var value = binding.LMuttonMeal.text.toString().toInt()
                                                                        value = value+1
                                                                        Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                        binding.LMuttonMeal.setText(value.toString())
                                                                    }
                                                                    if (meal.isSahari == true){
                                                                        var value = binding.SahariMeal.text.toString().toInt()
                                                                        value = value+1
                                                                        Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                        binding.SahariMeal.setText(value.toString())
                                                                    }
                                                                }
                                                                if (meal.dstatus == true) {
                                                                    var value = binding.DStudentMeal.text.toString().toInt()
                                                                    value = value+1
                                                                    Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                    binding.DStudentMeal.setText(value.toString())

                                                                    if (meal.disMutton == true){
                                                                        var value = binding.DMuttonMeal.text.toString().toInt()
                                                                        value = value+1
                                                                        Log.e("Firebase", "${hallId}: ${value.toString()}")
                                                                        binding.DMuttonMeal.setText(value.toString())
                                                                    }
                                                                }

                                                            }
                                                        }///
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {
                                                        load.end()
                                                        Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()

                                                    }
                                                })
                                        }
                                    }


                                    val dailyReport = DailyReport(month,day,dayMealStatus.isRamadan,
                                        binding.BStudentMeal.text.toString(),bg, binding.BMuttonMeal.text.toString(),
                                        binding.LStudentMeal.text.toString(),lg,binding.LMuttonMeal.text.toString(),binding.SahariMeal.text.toString(),
                                        binding.DStudentMeal.text.toString(),dg, binding.DMuttonMeal.text.toString())

                                    setReport(dailyReport)

                                    reportRef.child(month).child(day).setValue(dailyReport)
                                        .addOnSuccessListener {
                                            setReport(dailyReport)
                                        }
                                        .addOnFailureListener{

                                        }




                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error fetching hall data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching day meal status: ${error.message}")
            }
        })
    }



    private fun setdate() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.txtdate.text = dateFormat.format(calendar.time)
    }
    private fun validateDate(dateStr: String): Pair<Boolean, String> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val selectedDate = dateFormat.parse(dateStr) ?: return Pair(false, "Invalid date format!")

        val calendar = Calendar.getInstance()

        // Get today's date without time
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Get current time
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            selectedDate.after(today) -> Pair(false, "You can't check report for future date!")
            selectedDate == today && currentHour < 20 -> Pair(false, "You can check report after 8:00 PM.")
            else -> Pair(true, "Date is valid!")
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                binding.txtdate.text = dateFormat.format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun getDayName(dateString: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = sdf.parse(dateString)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dayFormat.format(date!!)
    }
}
