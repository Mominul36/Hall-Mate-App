package com.example.hallmate.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.DayMealStatusAdapter
import com.example.hallmate.Adapter.SMealStatusAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.DayMealStatusForRecycler
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.MealForRV
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentSHomeBinding
import com.example.hallmate.databinding.FragmentSMealStatusBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SMealStatusFragment : Fragment() {

    private lateinit var binding: FragmentSMealStatusBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    private lateinit var sMealStatusAdapter: SMealStatusAdapter
    private lateinit var mealForRVList: ArrayList<MealForRV>
    lateinit var load : Loading2
    var hallId :String = ""

    val array = Array(32) { i -> String.format("%02d", i) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSMealStatusBinding.inflate(inflater, container, false)
        load = Loading2(requireContext())
        auth = FirebaseAuth.getInstance()





        // RecyclerView setup with vertical scrolling
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mealForRVList = ArrayList()
        sMealStatusAdapter = SMealStatusAdapter(requireContext(),mealForRVList)
        binding.recyclerView.adapter = sMealStatusAdapter


        setButtonStatus(getCurrentMonth())

        fetchStudentMealStatus(getCurrentMonth())


        binding.feb25.setOnClickListener{
            setButtonStatus("02-2025")
            fetchStudentMealStatus("02-2025")
          //  fetchDayMealStatus("02-2025")

        }
        binding.mar25.setOnClickListener{
            setButtonStatus("03-2025")
            fetchStudentMealStatus("03-2025")        }

        binding.apr25.setOnClickListener{
            setButtonStatus("04-2025")
            fetchStudentMealStatus("04-2025")        }
        binding.may25.setOnClickListener{
            setButtonStatus("05-2025")
            fetchStudentMealStatus("05-2025")        }








        return binding.root





    }

    private fun fetchStudentMealStatus(month: String) {
        binding.main.visibility = View.GONE
        load.start()
        var sharedPreferences = requireContext().getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
         hallId = sharedPreferences.getString("hallId","").toString()


        val mealRef = database.getReference("Meal").child(month)
        val dayStatusRef = database.getReference("DayMealStatus").child(month)

        for(i in 1..31) {
            mealForRVList.clear()
            mealRef.child(array[i]).child(hallId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val breakFast = snapshot.child("BreakFast").getValue(Meal::class.java)
                            val lunch = snapshot.child("Lunch").getValue(Meal::class.java)
                            val dinner = snapshot.child("Dinner").getValue(Meal::class.java)
                            if (breakFast != null && lunch != null && dinner != null) {


                                dayStatusRef.child(array[i])
                                    .addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(daySnapshot: DataSnapshot) {
                                            val breakFast2 = daySnapshot.child("BreakFast").getValue(DayMealStatus::class.java)
                                            val lunch2 = daySnapshot.child("Lunch").getValue(DayMealStatus::class.java)
                                            val dinner2 = daySnapshot.child("Dinner").getValue(DayMealStatus::class.java)
                                            val isRamadan = daySnapshot.child("isRamadan").getValue(Boolean::class.java)


                                            val mealForRV = MealForRV(month, array[i], hallId,
                                                breakFast.status, dinner.status, lunch.status, lunch.isSahari,
                                                isRamadan,breakFast2?.status,lunch2?.status,dinner2?.status,
                                                breakFast2?.isAutoMeal,lunch2?.isAutoMeal,dinner2?.isAutoMeal,
                                                false,false
                                                )

//                                            Log.d("MealData", breakFast2.toString())
//                                            Log.d("MealData", lunch2.toString())
//                                            Log.d("MealData", dinner2.toString())
//                                            Log.d("MealData", isRamadan.toString())
//                                            Log.d("MealData", mealForRV.toString())

                                            mealForRVList.add(mealForRV)
                                            sMealStatusAdapter.notifyDataSetChanged()
                                            if(i==31){
                                                load.end()
                                                binding.main.visibility = View.VISIBLE

                                            }




                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            }
                        } else {
                            sMealStatusAdapter.notifyDataSetChanged()
                            load.end()
                            binding.main.visibility = View.VISIBLE

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })

        }






    }


    private fun setButtonStatus(currentMonth: String) {

        binding.feb25.setBackgroundResource(R.drawable.button_background_date)
        binding.feb25.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.mar25.setBackgroundResource(R.drawable.button_background_date)
        binding.mar25.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.apr25.setBackgroundResource(R.drawable.button_background_date)
        binding.apr25.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.may25.setBackgroundResource(R.drawable.button_background_date)
        binding.may25.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        if(currentMonth=="02-2025"){
            binding.feb25.setBackgroundResource(R.drawable.button_background_date2)
            binding.feb25.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }else if(currentMonth=="03-2025"){
            binding.mar25.setBackgroundResource(R.drawable.button_background_date2)
            binding.mar25.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }else if(currentMonth=="04-2025"){
            binding.apr25.setBackgroundResource(R.drawable.button_background_date2)
            binding.apr25.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }else if(currentMonth=="05-2025"){
            binding.may25.setBackgroundResource(R.drawable.button_background_date2)
            binding.may25.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }






    }

    fun getCurrentMonth(): String {
        val dateFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }



}