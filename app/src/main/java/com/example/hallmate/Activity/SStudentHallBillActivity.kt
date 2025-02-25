package com.example.hallmate.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.SMealStatusAdapter
import com.example.hallmate.Adapter.SStudentHallBillAdapter
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.MealForRV
import com.example.hallmate.Model.StudentHallBill
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMprofileBinding
import com.example.hallmate.databinding.ActivitySstudentHallBillBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SStudentHallBillActivity : AppCompatActivity() {
    lateinit var binding: ActivitySstudentHallBillBinding
    lateinit var auth : FirebaseAuth
    var database = FirebaseDatabase.getInstance()

    lateinit var hallId: String

    private lateinit var sStudentHallBillAdapter: SStudentHallBillAdapter
    private lateinit var hallBillList: ArrayList<StudentHallBill>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySstudentHallBillBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
        hallId = sharedPreferences.getString("hallId","").toString()

        setData()


        binding.back.setOnClickListener{
            finish()
        }





        // RecyclerView setup with vertical scrolling
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        hallBillList = ArrayList()
        sStudentHallBillAdapter = SStudentHallBillAdapter(this,hallBillList)
        binding.recyclerView.adapter = sStudentHallBillAdapter







    }

    private fun setData() {
        val (date, month) = getCurrentDateAndMonth()

        val dayMealStatusRef = database.getReference("DayMealStatus").child(month)
        val mealRef = database.getReference("Meal").child(month).child(hallId)

        val mealStatusList = mutableListOf<DayMealStatus>()
        val mealList = mutableListOf<Meal>()

        dayMealStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (daySnapshot in snapshot.children) {
                        val mealStatus = daySnapshot.getValue(DayMealStatus::class.java)
                        if (mealStatus != null) {
                            mealStatusList.add(mealStatus)
                        }
                    }


                    mealRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (mealSnapshot in snapshot.children) {
                                    val meal = mealSnapshot.getValue(Meal::class.java)
                                    if (meal != null) {
                                        mealList.add(meal)
                                    }
                                }



                                for(i in 0..mealList.size-1){
                                    var meal = mealList[i]
                                    var mealStatus = mealStatusList[i]



                                    var Tdate:String =""
                                    var Tbstatus:Boolean= false
                                    var Tlstatus:Boolean= false
                                    var Tdstatus:Boolean= false

                                    var TbisShowing:Boolean= false
                                    var TlisShowing:Boolean= false
                                    var TdisShowing:Boolean= false

                                    var TbCost:Double= 0.0
                                    var TlCost:Double=0.0
                                    var TdCost:Double=0.0
                                    var TtotalCost:Double=0.0







                                    var givenDate = meal.date+"-"+meal.month

                                    if(isDateBeforeToday(givenDate)){

                                    }else{
                                        var studentHallBill = StudentHallBill(meal.date,false,false,false,
                                            false,false,false, 0.0,0.0,0.0,0.0)
                                        hallBillList.add(studentHallBill)
                                        continue
                                    }

                                    //for breakfast
                                    if(meal.bstatus==false){
                                        Tbstatus = false
                                    }
                                    else{
                                        Tbstatus = true
                                        TbisShowing = true

                                        if(mealStatus.bmealCost==0.0 || mealStatus.bfuelCost==0.0 || (
                                                    mealStatus.bisMuttonOrBeaf==true && mealStatus.bextraMuttonCost==0.0)){

                                            TbisShowing = false
                                        }else{

                                            val denominator1 = (mealStatus.btotalMeal!! - mealStatus.bguestMeal!!).toDouble()
                                            val denominator2 = (mealStatus.bMuttonMeal!! - mealStatus.bguestMuttonMeal!!).toDouble()
                                            var cost1: Double = 0.0
                                            var cost2: Double = 0.0
                                            var cost3: Double = 0.0


                                            if (denominator1 != 0.0) {
                                                cost1 = String.format("%.2f", mealStatus.bmealCost!! / denominator1).toDouble()
                                                cost2 = String.format("%.2f", mealStatus.bfuelCost!! / denominator1).toDouble()
                                            } else {
                                                TbisShowing = false
                                            }

                                            if(mealStatus.bisMuttonOrBeaf==true && meal.bisMutton==true){
                                                if (denominator2 != 0.0) {
                                                    cost3 = String.format("%.2f", mealStatus.bextraMuttonCost!! / denominator2).toDouble()
                                                } else {
                                                    TbisShowing = false
                                                }
                                            }
                                            TbCost = cost1 + cost2 + cost3

                                        }



                                    }//////





                                    //for Lunch
                                    if(meal.lstatus==false){
                                        Tlstatus = false
                                    }
                                    else{
                                        Tlstatus = true
                                        TlisShowing = true

                                        if(mealStatus.lmealCost==0.0 || mealStatus.lfuelCost==0.0 || (
                                                    mealStatus.lisMuttonOrBeaf==true && mealStatus.lextraMuttonCost==0.0)){

                                            TlisShowing = false
                                        }else{

                                            val denominator1 = (mealStatus.ltotalMeal!! - mealStatus.lguestMeal!!).toDouble()
                                            val denominator2 = (mealStatus.lMuttonMeal!! - mealStatus.lguestMuttonMeal!!).toDouble()
                                            var cost1: Double = 0.0
                                            var cost2: Double = 0.0
                                            var cost3: Double = 0.0


                                            if (denominator1 != 0.0) {
                                                cost1 = String.format("%.2f", mealStatus.lmealCost!! / denominator1).toDouble()
                                                cost2 = String.format("%.2f", mealStatus.lfuelCost!! / denominator1).toDouble()
                                            } else {
                                                TlisShowing = false
                                            }

                                            if(mealStatus.lisMuttonOrBeaf==true && meal.lisMutton==true){
                                                if (denominator2 != 0.0) {
                                                    cost3 = String.format("%.2f", mealStatus.lextraMuttonCost!! / denominator2).toDouble()
                                                } else {
                                                    TlisShowing = false
                                                }
                                            }
                                            TlCost = cost1 + cost2 + cost3

                                        }



                                    }//////





                                    //for Dinner
                                    if(meal.dstatus==false){
                                        Tdstatus = false
                                    }
                                    else{
                                        Tdstatus = true
                                        TdisShowing = true

                                        if(mealStatus.dmealCost==0.0 || mealStatus.dfuelCost==0.0 || (
                                                    mealStatus.disMuttonOrBeaf==true && mealStatus.dextraMuttonCost==0.0)){

                                            TdisShowing = false
                                        }else{

                                            val denominator1 = (mealStatus.dtotalMeal!! - mealStatus.dguestMeal!!).toDouble()
                                            val denominator2 = (mealStatus.dMuttonMeal!! - mealStatus.dguestMuttonMeal!!).toDouble()
                                            var cost1: Double = 0.0
                                            var cost2: Double = 0.0
                                            var cost3: Double = 0.0


                                            if (denominator1 != 0.0) {
                                                cost1 = String.format("%.2f", mealStatus.dmealCost!! / denominator1).toDouble()
                                                cost2 = String.format("%.2f", mealStatus.dfuelCost!! / denominator1).toDouble()
                                            } else {
                                                TdisShowing = false
                                            }

                                            if(mealStatus.disMuttonOrBeaf==true && meal.disMutton==true){
                                                if (denominator2 != 0.0) {
                                                    cost3 = String.format("%.2f", mealStatus.dextraMuttonCost!! / denominator2).toDouble()
                                                } else {
                                                    TdisShowing = false
                                                }
                                            }
                                            TdCost = cost1 + cost2 + cost3

                                        }



                                    }//////



                                    TtotalCost = String.format("%.2f", TbCost!! + TlCost!! + TdCost!!).toDouble()

                                    var studentHallBill = StudentHallBill(meal.date,Tbstatus,Tlstatus,Tdstatus,
                                        TbisShowing,TlisShowing,TdisShowing, TbCost,TlCost,TdCost,TtotalCost)
                                    hallBillList.add(studentHallBill)







                                }



                                sStudentHallBillAdapter.notifyDataSetChanged()






                            } else {
                                Log.d("MealStatus", "No data available for this month")
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebaseError", "Error fetching meal status: ${error.message}")
                        }
                    })











                    /////////
                } else {
                    Log.d("MealStatus", "No data available for this month")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching meal status: ${error.message}")
            }
        })











    }



    fun isDateBeforeToday(dateString: String): Boolean {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return try {
            val inputDate = formatter.parse(dateString) // স্ট্রিং থেকে Date এ কনভার্ট
            val currentDate = formatter.parse(formatter.format(Date())) // বর্তমান তারিখ

            inputDate.before(currentDate) // যদি inputDate বর্তমান তারিখের আগে হয় তাহলে true, নাহলে false
        } catch (e: Exception) {
            e.printStackTrace()
            false // যদি কনভার্শনে কোনো সমস্যা হয়, তাহলে false রিটার্ন করবো
        }
    }
    fun getCurrentDateAndMonth(): Pair<String, String> {
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())

        val currentMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())

        return Pair(currentDate, currentMonthYear)
    }
}