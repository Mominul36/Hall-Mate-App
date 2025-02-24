package com.example.hallmate.Fragments

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.MStudentAdapter
import com.example.hallmate.Adapter.MenuStudentHomeAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Hall
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.Menu
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMStudentRequestBinding
import com.example.hallmate.databinding.FragmentSHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale


class SHomeFragment : Fragment() {

    private lateinit var binding: FragmentSHomeBinding
    private var database = FirebaseDatabase.getInstance()
    lateinit var hallId :String

    lateinit var load : Loading

    private lateinit var menuStudentHomeAdapter: MenuStudentHomeAdapter
    private lateinit var menuList: ArrayList<Menu>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSHomeBinding.inflate(inflater, container, false)
        load = Loading(requireContext())
        var sharedPreferences = requireContext().getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
        hallId = sharedPreferences.getString("hallId","").toString()



        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        menuList = ArrayList()
        menuStudentHomeAdapter = MenuStudentHomeAdapter(menuList)
        binding.recyclerView.adapter = menuStudentHomeAdapter



        checkTime()

        binding.breakfast.setOnClickListener{
            load.start()
            refReshMenu("B")
        }
        binding.lunch.setOnClickListener{
            load.start()
            refReshMenu("L")
        }
        binding.iftar.setOnClickListener{
            load.start()
            refReshMenu("I")
        }
        binding.dinner.setOnClickListener{
            load.start()
            refReshMenu("D")
        }


















        return binding.root

    }

    private fun checkTime() {
        load.start()
        val (date, month) = getCurrentDateAndMonth()

        val dayMealStatusRef = database.getReference("DayMealStatus")
        val hallRef = database.getReference("Hall")
        val mealRef = database.getReference("Meal")




        hallRef
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val hall = snapshot.getValue(Hall::class.java)
                        if (hall != null) {

                            dayMealStatusRef.child(month).child(date)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)
                                            if(dayMealStatus!=null){
                                                mealRef.child(month).child(hallId).child(date)
                                                    .addListenerForSingleValueEvent(object: ValueEventListener{
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if(snapshot.exists()){
                                                                val meal = snapshot.getValue(Meal::class.java)
                                                                if(meal!=null){

                                                                    val currentTime = getCurrentTime()
                                                                    val sahariStartTime = convertStringToTime(hall.sahariStartTime.toString())
                                                                    var sahariFinishTime= convertStringToTime(hall.sahariFinishTime.toString())
                                                                    var breakfastStartTime= convertStringToTime(hall.breakfastStartTime.toString())
                                                                    var breakfastFinishTime= convertStringToTime(hall.breakfastFinishTime.toString())
                                                                    var lunchStartTime= convertStringToTime(hall.lunchStartTime.toString())
                                                                    var lunchFinishTime= convertStringToTime(hall.lunchFinishTime.toString())
                                                                    var iftarStartTime= convertStringToTime(hall.iftarStartTime.toString())
                                                                    var iftarFinishTime= convertStringToTime(hall.iftarFinishTime.toString())
                                                                    var dinnerStartTime= convertStringToTime(hall.dinnerStartTime.toString())
                                                                    var dinnerFinishTime= convertStringToTime(hall.dinnerFinishTime.toString())
                                                                    if (dayMealStatus.isRamadan == true) {
                                                                        if(meal.isSahari==true){ // Muslim

                                                                            if(currentTime.isBefore(sahariFinishTime) || currentTime.equals(sahariFinishTime)){
                                                                                setMenu(hall,"Sahari")
                                                                            }else if(currentTime.isBefore(iftarFinishTime) || currentTime.equals(iftarFinishTime)){
                                                                                setMenu(hall,"Iftar")
                                                                            }else if(currentTime.isBefore(dinnerFinishTime) || currentTime.equals(dinnerFinishTime)){
                                                                                setMenu(hall,"Dinner")
                                                                            }else{
                                                                                setMenu(hall,"Don't Show")
                                                                            }

                                                                        }else{ //meal.isSahari==true // Hindu

                                                                            if(currentTime.isBefore(lunchFinishTime) || currentTime.equals(lunchFinishTime)){
                                                                                setMenu(hall,"Lunch")
                                                                            }else{
                                                                                if(meal.bstatus==true){
                                                                                    if(currentTime.isBefore(iftarFinishTime) || currentTime.equals(iftarFinishTime)){
                                                                                        setMenu(hall,"Iftar")
                                                                                    }else if(currentTime.isBefore(dinnerFinishTime) || currentTime.equals(dinnerFinishTime)){
                                                                                        setMenu(hall,"Dinner")
                                                                                    }else{
                                                                                        setMenu(hall,"Don't Show")
                                                                                    }

                                                                                }else{
                                                                                    if(currentTime.isBefore(dinnerFinishTime) || currentTime.equals(dinnerFinishTime)){
                                                                                        setMenu(hall,"Dinner")
                                                                                    }else{
                                                                                        setMenu(hall,"Don't Show")
                                                                                    }


                                                                                }
                                                                            }///

                                                                        }

                                                                    } else {///dayMealStatus.isRamadan == true
                                                                        if(currentTime.isBefore(breakfastFinishTime) || currentTime.equals(breakfastFinishTime)){
                                                                            setMenu(hall,"BreakFast")
                                                                        }else if(currentTime.isBefore(lunchFinishTime) || currentTime.equals(lunchFinishTime)){
                                                                            setMenu(hall,"Lunch")
                                                                        }else if(currentTime.isBefore(dinnerFinishTime) || currentTime.equals(dinnerFinishTime)){
                                                                            setMenu(hall,"Dinner")
                                                                        }else{
                                                                            setMenu(hall,"Don't Show")
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            load.end()
                                                            Toast.makeText(requireContext(),"Internet Error",Toast.LENGTH_SHORT).show()
                                                        }

                                                    })

                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        load.end()
                                        Toast.makeText(requireContext(),"Internet Error",Toast.LENGTH_SHORT).show()
                                    }

                                })


                        }

                    }else{
                        load.end()
                        Toast.makeText(requireContext(),"Something Wrong",Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    load.end()
                    Toast.makeText(requireContext(),"Internet Error",Toast.LENGTH_SHORT).show()

                }

            })



    }


    fun refReshMenu(button:String){
        val (date, month) = getCurrentDateAndMonth()
        val dayMealStatusRef = database.getReference("DayMealStatus")
        val hallRef = database.getReference("Hall")


        hallRef
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val hall = snapshot.getValue(Hall::class.java)
                        if (hall != null) {

                            dayMealStatusRef.child(month).child(date)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val dayMealStatus = snapshot.getValue(DayMealStatus::class.java)
                                            if(dayMealStatus!=null){
                                                if(dayMealStatus.isRamadan==true){

                                                    if(button=="B"){
                                                        load.end()
                                                        Toast.makeText(requireContext(),"There is no meal for breakfast today.",Toast.LENGTH_SHORT).show()
                                                    }else if(button=="L"){
                                                        setButton("Lunch")
                                                        setMenu(hall,"Lunch")
                                                    }else if(button=="I"){
                                                        setButton("Iftar")
                                                        setMenu(hall,"Iftar")
                                                    }else if(button=="D"){
                                                        setButton("Dinner")
                                                        setMenu(hall,"Dinner")
                                                    }


                                                }else{
                                                    if(button=="B"){
                                                        setButton("BreakFast")
                                                        setMenu(hall,"BreakFast")
                                                    }else if(button=="L"){
                                                        setButton("Lunch")
                                                        setMenu(hall,"Lunch")
                                                    }else if(button=="I"){
                                                        load.end()
                                                        Toast.makeText(requireContext(),"There is no meal for iftar today.",Toast.LENGTH_SHORT).show()
                                                    }else if(button=="D"){
                                                        setButton("Dinner")
                                                        setMenu(hall,"Dinner")
                                                    }

                                                }


                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        load.end()
                                        Toast.makeText(requireContext(),"Something Wrong",Toast.LENGTH_SHORT).show()
                                    }

                                })


                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    load.end()
                    Toast.makeText(requireContext(),"Something Wrong",Toast.LENGTH_SHORT).show()
                }

            })









    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMenu(hall: Hall, period: String) {
        val (date, month) = getCurrentDateAndMonth()
        setButton(period)

        if(period=="Don't Show"){
            binding.periodName.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            binding.note.visibility = View.GONE
            binding.note2.visibility = View.GONE
            binding.dot.visibility = View.GONE
            load.end()
            return
        }


        binding.periodName.setText(period+" Menu")

        if(period=="BreakFast"){
            binding.note.setText(convertTimeFormat(hall.breakfastStartTime.toString()) + " to " + convertTimeFormat(hall.breakfastFinishTime.toString()))
        }else if(period=="Lunch"){
            binding.note.setText(convertTimeFormat(hall.lunchStartTime.toString()) + " to " + convertTimeFormat(hall.lunchFinishTime.toString()))
        }else if(period=="Sahari"){
            binding.note.setText(convertTimeFormat(hall.sahariStartTime.toString()) + " to " + convertTimeFormat(hall.sahariFinishTime.toString()))
        }else if(period=="Iftar"){
            binding.note.setText(convertTimeFormat(hall.iftarStartTime.toString()) + " to " + convertTimeFormat(hall.iftarFinishTime.toString()))
        }else if(period=="Dinner"){
            binding.note.setText(convertTimeFormat(hall.dinnerStartTime.toString()) + " to " + convertTimeFormat(hall.dinnerFinishTime.toString()))
        }



        val menuRef = database.getReference("Menu")


        menuRef.child(getCurrentDayOfWeek().toString()).child(period).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    menuList.clear()
                    for(data in snapshot.children){
                        val menu = data.getValue(Menu::class.java)
                        menu?.let {
                           menuList.add(it)
                        }
                    }
              menuStudentHomeAdapter.notifyDataSetChanged()
                    load.end()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                load.end()
                Toast.makeText(requireContext(),"Something Wrong",Toast.LENGTH_SHORT).show()
            }

        })









    }


    fun convertTimeFormat(inputTime: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())  // Parse the 24-hour time format
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault()) // Format it to 12-hour format with AM/PM

        val date: Date? = inputFormat.parse(inputTime)  // Parse the input time to Date object
        return if (date != null) {
            outputFormat.format(date)  // Convert the Date to desired output format
        } else {
            "Invalid time"
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayOfWeek(): String {
        val today = LocalDate.now()  // Get the current date
        val dayOfWeek = today.dayOfWeek  // Get the day of the week
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())  // Return the day in full text (e.g., "Sunday")
    }


    private fun setButton(period: String) {
       binding.breakfast.setBackgroundResource(R.drawable.period_background)
        binding.lunch.setBackgroundResource(R.drawable.period_background)
        binding.iftar.setBackgroundResource(R.drawable.period_background)
        binding.dinner.setBackgroundResource(R.drawable.period_background)

        if(period=="BreakFast"){
            binding.breakfast.setBackgroundResource(R.drawable.period_background2)
        }else if(period=="Sahari" || period=="Lunch"){
            binding.lunch.setBackgroundResource(R.drawable.period_background2)
        }else if(period=="Iftar" ){
            binding.iftar.setBackgroundResource(R.drawable.period_background2)
        }else if(period=="Dinner" ){
            binding.dinner.setBackgroundResource(R.drawable.period_background2)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToTime(timeString: String): LocalTime {
        // Define the time format
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        // Convert string to LocalTime
        return LocalTime.parse(timeString, formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): LocalTime {
        val currentTime = LocalTime.now()

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedTime = currentTime.format(formatter)

        return LocalTime.parse(formattedTime, formatter)
    }
    fun getCurrentDateAndMonth(): Pair<String, String> {
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())

        val currentMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())

        return Pair(currentDate, currentMonthYear)
    }





}