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
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.DayMealStatus
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

class SMealStatusFragment : Fragment(), DialogDismissListener {

    private lateinit var binding: FragmentSMealStatusBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    private lateinit var sMealStatusAdapter: SMealStatusAdapter
    private lateinit var mealForRVList: ArrayList<MealForRV>
    lateinit var load2 : Loading2
    lateinit var load : Loading
    lateinit var success : SuccessDialog
    var hallId :String = ""
    var searchMonth :String = ""

    val array = Array(32) { i -> String.format("%02d", i) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSMealStatusBinding.inflate(inflater, container, false)
        load2 = Loading2(requireContext())
        load = Loading(requireContext())
        auth = FirebaseAuth.getInstance()
        success = SuccessDialog(requireContext(), this)


        binding.save.setOnClickListener {
            saveUpdatedData()
        }




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

        }
        binding.mar25.setOnClickListener{
            setButtonStatus("03-2025")
            fetchStudentMealStatus("03-2025")
        }

        binding.apr25.setOnClickListener{
            setButtonStatus("04-2025")
            fetchStudentMealStatus("04-2025")
        }
        binding.may25.setOnClickListener{
            setButtonStatus("05-2025")
            fetchStudentMealStatus("05-2025")
        }








        return binding.root





    }

    private fun saveUpdatedData() {
        load.start()

        val updatedData = sMealStatusAdapter.getUpdatedMealData()
        var databaseRef = database.getReference("Meal").child(searchMonth).child(hallId)

        for (mealForRV in updatedData) {

            val meal: Map<String, Any?> = mapOf(
                "isSahari" to mealForRV.isSahari ,
                "bstatus" to mealForRV.bstatus,
                "lstatus" to mealForRV.lstatus,
                "dstatus" to mealForRV.dstatus
            )


            databaseRef.child(mealForRV.date.toString()).updateChildren(meal)
                .addOnSuccessListener {

                }
                .addOnFailureListener { exception ->
                    load.end()
                    Toast.makeText(context, "Failed to update: ${exception.message}", Toast.LENGTH_SHORT).show()
                }


        }


        load.end()
        success.show("Success","Meal data updated successfully",true,"")


    }













    private fun fetchStudentMealStatus(month: String) {
        searchMonth = month
        binding.main.visibility = View.GONE
        load.start()
        var sharedPreferences = requireContext().getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
         hallId = sharedPreferences.getString("hallId","").toString()

        val mealRef = database.getReference("Meal")
        val dayStatusRef = database.getReference("DayMealStatus")


        var mealList = mutableListOf<Meal>() // Meal Data Store করার জন্য List



        mealRef.child(month).child(hallId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

                    mealList.clear()
                    for (mealSnapshot in snapshot.children) {
                        val meal = mealSnapshot.getValue(Meal::class.java)
                        if (meal!=null) {
                            mealList.add(meal)
                        }

                        Log.d("FirebaseData", meal.toString())
                    }



                    var dayMealStatusList = mutableListOf<DayMealStatus>()

                    dayStatusRef.child(month)
                        .addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){


                                dayMealStatusList.clear()

                                for (dayStatusSnapshot in snapshot.children) {
                                    val dayMealStatus = dayStatusSnapshot.getValue(DayMealStatus::class.java)
                                    if (dayMealStatus!=null) {
                                        dayMealStatusList.add(dayMealStatus)
                                    }
                                }

                                mealForRVList.clear()
                                var len:Int = dayMealStatusList.size-1

                                for(i in 0..len){
                                   var mealForRV = MealForRV(month,mealList[i].date,hallId,mealList[i].bstatus,mealList[i].lstatus,mealList[i].dstatus,
                                       mealList[i].isSahari,dayMealStatusList[i].isRamadan,dayMealStatusList[i].bstatus,dayMealStatusList[i].lstatus,
                                       dayMealStatusList[i].dstatus, dayMealStatusList[i].bisAutoMeal, dayMealStatusList[i].lisAutoMeal,dayMealStatusList[i].disAutoMeal,
                                       false,false)

                                    mealForRVList.add(mealForRV)


                                }

                                sMealStatusAdapter.notifyDataSetChanged()

                                load.end()
                                binding.main.visibility = View.VISIBLE

                                }
                            }///////

                            override fun onCancelled(error: DatabaseError) {
                                load.end()
                               // binding.main.visibility = View.VISIBLE
                                Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()
                            }
                        })

                    }
                }/////
                override fun onCancelled(error: DatabaseError) {
                    load.end()
                   // binding.main.visibility = View.VISIBLE
                    Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()
                }
            })









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

    override fun onDialogDismissed(message: String) {



    }


}