package com.example.hallmate.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hallmate.Activity.MStudentProfileActivity
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.DayMealStatusForRecycler
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.google.firebase.database.FirebaseDatabase

class DayMealStatusAdapter(
    private val context: Context,
    private val dayMealStatusForRecyclerList: ArrayList<DayMealStatusForRecycler>,
) : RecyclerView.Adapter<DayMealStatusAdapter.DayMealStatusViewHolder>() {

    var  bStatus: Boolean = false
    var bIsBeafOrMutton: Boolean= false
    var bIsAutoMeal: Boolean= false

    var  lStatus: Boolean = false
    var lIsBeafOrMutton: Boolean= false
    var lIsAutoMeal: Boolean= false

    var  dStatus: Boolean = false
    var dIsBeafOrMutton: Boolean= false
    var dIsAutoMeal: Boolean= false

     var load = Loading(context)


    class DayMealStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val swRamadan: Switch = itemView.findViewById(R.id.swRamadan)
        val btnSave: ImageView = itemView.findViewById(R.id.btnSave)

        val bBtnStatus: ImageView = itemView.findViewById(R.id.bBtnStatus)
        val bBtnIsBeafOrMutton: ImageView = itemView.findViewById(R.id.bBtnIsBeafOrMutton)
        val bBtnIsAutoMeal: ImageView = itemView.findViewById(R.id.bBtnIsAutoMeal)

        val bEditMealCost: EditText = itemView.findViewById(R.id.bEditMealCost)
        val bEditFuelCost: EditText = itemView.findViewById(R.id.bEditFuelCost)
        val bEditExtraMuttonCost: EditText = itemView.findViewById(R.id.bEditExtraMuttonCost)

        val lBtnStatus: ImageView = itemView.findViewById(R.id.lBtnStatus)
        val lBtnIsBeafOrMutton: ImageView = itemView.findViewById(R.id.lBtnIsBeafOrMutton)
        val lBtnIsAutoMeal: ImageView = itemView.findViewById(R.id.lBtnIsAutoMeal)

        val lEditMealCost: EditText = itemView.findViewById(R.id.lEditMealCost)
        val lEditFuelCost: EditText = itemView.findViewById(R.id.lEditFuelCost)
        val lEditExtraMuttonCost: EditText = itemView.findViewById(R.id.lEditExtraMuttonCost)

        val dBtnStatus: ImageView = itemView.findViewById(R.id.dBtnStatus)
        val dBtnIsBeafOrMutton: ImageView = itemView.findViewById(R.id.dBtnIsBeafOrMutton)
        val dBtnIsAutoMeal: ImageView = itemView.findViewById(R.id.dBtnIsAutoMeal)

        val dEditMealCost: EditText = itemView.findViewById(R.id.dEditMealCost)
        val dEditFuelCost: EditText = itemView.findViewById(R.id.dEditFuelCost)
        val dEditExtraMuttonCost: EditText = itemView.findViewById(R.id.dEditExtraMuttonCost)






    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayMealStatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_status, parent, false)
        return DayMealStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayMealStatusViewHolder, position: Int) {
        val dayMealStatusForRecycler = dayMealStatusForRecyclerList[position]


        holder.btnSave.setOnClickListener{
            saveData(holder,position)
        }


        holder.txtDate.text = dayMealStatusForRecycler.date
        holder.swRamadan.isChecked = dayMealStatusForRecycler.isRamadan ?: false

        if (dayMealStatusForRecycler.bstatus == true) {
            holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
            bStatus = true
        } else {
            holder.bBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (dayMealStatusForRecycler.bisMuttonOrBeaf == true) {
            holder.bBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark)
            bIsBeafOrMutton = true

        } else {
            holder.bBtnIsBeafOrMutton.setImageDrawable(null) // Remove image if not checked
            holder.bEditExtraMuttonCost.isEnabled  = false
        }

        if (dayMealStatusForRecycler.bisAutoMeal == true) {
            holder.bBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark) // set auto meal image
            bIsAutoMeal = true
        } else {
            holder.bBtnIsAutoMeal.setImageDrawable(null) // Remove image if not checked
        }

        // Set the meal costs
        holder.bEditMealCost.setText(dayMealStatusForRecycler.bmealCost?.toString() ?: "")
        holder.bEditFuelCost.setText(dayMealStatusForRecycler.bfuelCost?.toString() ?: "")
        holder.bEditExtraMuttonCost.setText(dayMealStatusForRecycler.bextraMuttonCost?.toString() ?: "")

        // Set the lunch status and other values
        if (dayMealStatusForRecycler.lstatus == true) {
            holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
            lStatus = true// set the checked image
        } else {
            holder.lBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (dayMealStatusForRecycler.lisMuttonOrBeaf == true) {
            holder.lBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark) // set mutton or beef image
            lIsBeafOrMutton = true
        } else {
            holder.lBtnIsBeafOrMutton.setImageDrawable(null) // Remove image if not checked
            holder.lEditExtraMuttonCost.isEnabled  = false
        }

        if (dayMealStatusForRecycler.lisAutoMeal == true) {
            holder.lBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark) // set auto meal image
            lIsAutoMeal = true
        } else {
            holder.lBtnIsAutoMeal.setImageDrawable(null) // Remove image if not checked
        }

        // Set the meal costs for lunch
        holder.lEditMealCost.setText(dayMealStatusForRecycler.lmealCost?.toString() ?: "")
        holder.lEditFuelCost.setText(dayMealStatusForRecycler.lfuelCost?.toString() ?: "")
        holder.lEditExtraMuttonCost.setText(dayMealStatusForRecycler.lextraMuttonCost?.toString() ?: "")

        // Set the dinner status and other values
        if (dayMealStatusForRecycler.dstatus == true) {
            holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)
            dStatus = true// set the checked image
        } else {
            holder.dBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (dayMealStatusForRecycler.disMuttonOrBeaf == true) {
            holder.dBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark) // set mutton or beef image
            dIsBeafOrMutton = true
        } else {
            holder.dBtnIsBeafOrMutton.setImageDrawable(null) // Remove image if not checked
            holder.dEditExtraMuttonCost.isEnabled  = false
        }

        if (dayMealStatusForRecycler.disAutoMeal == true) {
            holder.dBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark) // set auto meal image
            dIsAutoMeal = true
        } else {
            holder.dBtnIsAutoMeal.setImageDrawable(null) // Remove image if not checked
        }

        // Set the meal costs for dinner
        holder.dEditMealCost.setText(dayMealStatusForRecycler.dmealCost?.toString() ?: "")
        holder.dEditFuelCost.setText(dayMealStatusForRecycler.dfuelCost?.toString() ?: "")
        holder.dEditExtraMuttonCost.setText(dayMealStatusForRecycler.dextraMuttonCost?.toString() ?: "")



        holder.bBtnStatus.setOnClickListener{
            if(bStatus)
                holder.bBtnStatus.setImageDrawable(null)
            else
                holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
            bStatus = !bStatus
        }

        holder.bBtnIsBeafOrMutton.setOnClickListener{
            if(bIsBeafOrMutton){
                holder.bBtnIsBeafOrMutton.setImageDrawable(null)
                holder.bEditExtraMuttonCost.isEnabled  = false
            }
            else{
                holder.bBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark)
                holder.bEditExtraMuttonCost.isEnabled  = true
            }
            bIsBeafOrMutton = !bIsBeafOrMutton
        }

        holder.bBtnIsAutoMeal.setOnClickListener{
            if(bIsAutoMeal)
                holder.bBtnIsAutoMeal.setImageDrawable(null)
            else
                holder.bBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark)
            bIsAutoMeal = !bIsAutoMeal
        }


        holder.lBtnStatus.setOnClickListener{
            if(lStatus)
                holder.lBtnStatus.setImageDrawable(null)
            else
                holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
            lStatus = !lStatus
        }

        holder.lBtnIsBeafOrMutton.setOnClickListener{
            if(lIsBeafOrMutton){
                holder.lBtnIsBeafOrMutton.setImageDrawable(null)
                holder.lEditExtraMuttonCost.isEnabled  = false
            }
            else{
                holder.lBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark)
                holder.lEditExtraMuttonCost.isEnabled  = true
            }
            lIsBeafOrMutton = !lIsBeafOrMutton
        }

        holder.lBtnIsAutoMeal.setOnClickListener{
            if(lIsAutoMeal)
                holder.lBtnIsAutoMeal.setImageDrawable(null)
            else
                holder.lBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark)
            lIsAutoMeal = !lIsAutoMeal
        }


        holder.dBtnStatus.setOnClickListener{
            if(dStatus)
                holder.dBtnStatus.setImageDrawable(null)
            else
                holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)
            dStatus = !dStatus
        }

        holder.dBtnIsBeafOrMutton.setOnClickListener{
            if(dIsBeafOrMutton){
                holder.dBtnIsBeafOrMutton.setImageDrawable(null)
                holder.dEditExtraMuttonCost.isEnabled  = false
            }
            else{
                holder.dBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark)
                holder.dEditExtraMuttonCost.isEnabled  = true
            }
            dIsBeafOrMutton = !dIsBeafOrMutton
        }

        holder.dBtnIsAutoMeal.setOnClickListener{
            if(dIsAutoMeal)
                holder.dBtnIsAutoMeal.setImageDrawable(null)
            else
                holder.dBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark)
            dIsAutoMeal = !dIsAutoMeal
        }






    }

    private fun saveData(holder: DayMealStatusAdapter.DayMealStatusViewHolder, position: Int) {
        load.start()
        val dayMealStatusForRecycler = dayMealStatusForRecyclerList[position]
        val database = FirebaseDatabase.getInstance()
        val dateReference = database.getReference("DayMealStatus").child(dayMealStatusForRecycler.month.toString()).child(dayMealStatusForRecycler.date.toString())

// আপডেট করা ডেটা তৈরি করছি
        val updateData = mapOf(
            "BreakFast" to DayMealStatus(
                date = dayMealStatusForRecycler.date.toString(),
                period = "BreakFast",
                status = bStatus,
                isMuttonOrBeaf = bIsBeafOrMutton,
                isAutoMeal = bIsAutoMeal,
                mealCost = holder.bEditMealCost.text.toString().toDouble(),
                fuelCost = holder.bEditFuelCost.text.toString().toDouble(),
                extraMuttonCost = holder.bEditExtraMuttonCost.text.toString().toDouble(),
                guestMeal = 0
            ),
            "Lunch" to DayMealStatus(
                date = dayMealStatusForRecycler.date.toString(),
                period = "Lunch",
                status = lStatus,
                isMuttonOrBeaf = lIsBeafOrMutton,
                isAutoMeal = lIsAutoMeal,
                mealCost = holder.lEditMealCost.text.toString().toDouble(),
                fuelCost = holder.lEditFuelCost.text.toString().toDouble(),
                extraMuttonCost = holder.lEditExtraMuttonCost.text.toString().toDouble(),
                guestMeal = 0
            ),
            "Dinner" to DayMealStatus(
                date = dayMealStatusForRecycler.date.toString(),
                period = "Dinner",
                status = dStatus,
                isMuttonOrBeaf = dIsBeafOrMutton,
                isAutoMeal = dIsAutoMeal,
                mealCost = holder.dEditMealCost.text.toString().toDouble(),
                fuelCost = holder.dEditFuelCost.text.toString().toDouble(),
                extraMuttonCost = holder.dEditExtraMuttonCost.text.toString().toDouble(),
                guestMeal = 0
            ),
            "isRamadan" to holder.swRamadan.isChecked
        )


        dateReference.updateChildren(updateData).addOnSuccessListener {
            load.end()
            Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { error ->
            Toast.makeText(context,"Update\", \"Error: ${error.message}",Toast.LENGTH_SHORT).show()
        }





    }


    override fun getItemCount(): Int = dayMealStatusForRecyclerList.size
}
