package com.example.hallmate.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.DayMealStatusAdapter
import com.example.hallmate.Adapter.StudentRequestAdapter
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.DayMealStatusForRecycler
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityDayMealStatusBinding
import com.example.hallmate.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayMealStatusActivity : AppCompatActivity() {

    lateinit var binding : ActivityDayMealStatusBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    private lateinit var dayMealStatusForRecyclerAdapter: DayMealStatusAdapter
    private lateinit var dayMealStatusForRecyclerList: ArrayList<DayMealStatusForRecycler>

    lateinit var load : Loading2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDayMealStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        load = Loading2(this)



        // RecyclerView setup with vertical scrolling
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        dayMealStatusForRecyclerList = ArrayList()
        dayMealStatusForRecyclerAdapter = DayMealStatusAdapter(this,dayMealStatusForRecyclerList)
        binding.recyclerView.adapter = dayMealStatusForRecyclerAdapter


        fetchDayMealStatus(getCurrentMonth())
        setButtonStatus(getCurrentMonth())

        binding.feb25.setOnClickListener{
            setButtonStatus("02-2025")
            fetchDayMealStatus("02-2025")

        }
        binding.mar25.setOnClickListener{
            setButtonStatus("03-2025")
            fetchDayMealStatus("03-2025")
        }

        binding.apr25.setOnClickListener{
            setButtonStatus("04-2025")
            fetchDayMealStatus("04-2025")
        }
        binding.may25.setOnClickListener{
            setButtonStatus("05-2025")
            fetchDayMealStatus("05-2025")
        }
        binding.back.setOnClickListener{
            finish()
        }








    }

    private fun setButtonStatus(currentMonth: String) {

            binding.feb25.setBackgroundResource(R.drawable.button_background_date)
            binding.feb25.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.mar25.setBackgroundResource(R.drawable.button_background_date)
            binding.mar25.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.apr25.setBackgroundResource(R.drawable.button_background_date)
            binding.apr25.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.may25.setBackgroundResource(R.drawable.button_background_date)
            binding.may25.setTextColor(ContextCompat.getColor(this, R.color.black))

        if(currentMonth=="02-2025"){
            binding.feb25.setBackgroundResource(R.drawable.button_background_date2)
            binding.feb25.setTextColor(ContextCompat.getColor(this, R.color.white))
        }else if(currentMonth=="03-2025"){
            binding.mar25.setBackgroundResource(R.drawable.button_background_date2)
            binding.mar25.setTextColor(ContextCompat.getColor(this, R.color.white))
        }else if(currentMonth=="04-2025"){
            binding.apr25.setBackgroundResource(R.drawable.button_background_date2)
            binding.apr25.setTextColor(ContextCompat.getColor(this, R.color.white))
        }else if(currentMonth=="05-2025"){
            binding.may25.setBackgroundResource(R.drawable.button_background_date2)
            binding.may25.setTextColor(ContextCompat.getColor(this, R.color.white))
        }






    }

    fun getCurrentMonth(): String {
        val dateFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
    private fun fetchDayMealStatus(month: String) {
        load.start()

        val monthReference = database.getReference("DayMealStatus").child(month)
        monthReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dayMealStatusForRecyclerList.clear()

                for (dateSnapshot in snapshot.children) {
                    val date = dateSnapshot.key // get the date key (e.g., "2024-02-15")

                    val isRamadan = dateSnapshot.child("isRamadan").getValue(Boolean::class.java) ?: false

                    val breakFast = dateSnapshot.child("BreakFast").getValue(DayMealStatus::class.java)
                    val lunch = dateSnapshot.child("Lunch").getValue(DayMealStatus::class.java)
                    val dinner = dateSnapshot.child("Dinner").getValue(DayMealStatus::class.java)

                    // Only add the data for that date once
                    if (breakFast != null || lunch != null || dinner != null) {
                        val dayMealStatusForRecycler = DayMealStatusForRecycler(
                            date = date,
                            month = month,
                            isRamadan = isRamadan,
                            bstatus = breakFast?.status,
                            bisMuttonOrBeaf = breakFast?.isMuttonOrBeaf,
                            bisAutoMeal = breakFast?.isAutoMeal,
                            bmealCost = breakFast?.mealCost,
                            bfuelCost = breakFast?.fuelCost,
                            bextraMuttonCost = breakFast?.extraMuttonCost,

                            lstatus = lunch?.status,
                            lisMuttonOrBeaf = lunch?.isMuttonOrBeaf,
                            lisAutoMeal = lunch?.isAutoMeal,
                            lmealCost = lunch?.mealCost,
                            lfuelCost = lunch?.fuelCost,
                            lextraMuttonCost = lunch?.extraMuttonCost,

                            dstatus = dinner?.status,
                            disMuttonOrBeaf = dinner?.isMuttonOrBeaf,
                            disAutoMeal = dinner?.isAutoMeal,
                            dmealCost = dinner?.mealCost,
                            dfuelCost = dinner?.fuelCost,
                            dextraMuttonCost = dinner?.extraMuttonCost
                        )

                        dayMealStatusForRecyclerList.add(dayMealStatusForRecycler)
                    }
                }

                // Notify the adapter that the data has changed
                dayMealStatusForRecyclerAdapter.notifyDataSetChanged()
                load.end()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any cancellation errors here
            }
        })
    }

}