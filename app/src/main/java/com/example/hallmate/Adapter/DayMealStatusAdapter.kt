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
import com.example.hallmate.Activity.DayStatusUpdateActivity
import com.example.hallmate.Activity.MStudentProfileActivity
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.google.firebase.database.FirebaseDatabase

class DayMealStatusAdapter(
    private val context: Context,
    private val dayMealStatusList: ArrayList<DayMealStatus>,
) : RecyclerView.Adapter<DayMealStatusAdapter.DayMealStatusViewHolder>() {


     var load = Loading(context)


    class DayMealStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val edit: ImageView = itemView.findViewById(R.id.edit)
        val swRamadan: Switch = itemView.findViewById(R.id.swRamadan)

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
        val dayMealStatus = dayMealStatusList[position]



        holder.txtDate.text = dayMealStatus.date
        holder.swRamadan.isChecked = dayMealStatus.isRamadan ?: false
        holder.swRamadan.isClickable = false

        if (dayMealStatus.bstatus == true) {
            holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
        } else {
            holder.bBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (dayMealStatus.bisMuttonOrBeaf == true) {
            holder.bBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark)

        } else {
            holder.bBtnIsBeafOrMutton.setImageDrawable(null) // Remove image if not checked
            holder.bEditExtraMuttonCost.isEnabled  = false
        }

        if (dayMealStatus.bisAutoMeal == true) {
            holder.bBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark) // set auto meal image
        } else {
            holder.bBtnIsAutoMeal.setImageDrawable(null) // Remove image if not checked
        }

        // Set the meal costs
        holder.bEditMealCost.setText(dayMealStatus.bmealCost?.toString() ?: "")
        holder.bEditFuelCost.setText(dayMealStatus.bfuelCost?.toString() ?: "")
        holder.bEditExtraMuttonCost.setText(dayMealStatus.bextraMuttonCost?.toString() ?: "")

        // Set the lunch status and other values
        if (dayMealStatus.lstatus == true) {
            holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
        } else {
            holder.lBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (dayMealStatus.lisMuttonOrBeaf == true) {
            holder.lBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark) // set mutton or beef image

        } else {
            holder.lBtnIsBeafOrMutton.setImageDrawable(null) // Remove image if not checked
            holder.lEditExtraMuttonCost.isEnabled  = false
        }

        if (dayMealStatus.lisAutoMeal == true) {
            holder.lBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark) // set auto meal image

        } else {
            holder.lBtnIsAutoMeal.setImageDrawable(null) // Remove image if not checked
        }

        // Set the meal costs for lunch
        holder.lEditMealCost.setText(dayMealStatus.lmealCost?.toString() ?: "")
        holder.lEditFuelCost.setText(dayMealStatus.lfuelCost?.toString() ?: "")
        holder.lEditExtraMuttonCost.setText(dayMealStatus.lextraMuttonCost?.toString() ?: "")

        // Set the dinner status and other values
        if (dayMealStatus.dstatus == true) {
            holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)

        } else {
            holder.dBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (dayMealStatus.disMuttonOrBeaf == true) {
            holder.dBtnIsBeafOrMutton.setImageResource(R.drawable.ic_tikmark) // set mutton or beef image

        } else {
            holder.dBtnIsBeafOrMutton.setImageDrawable(null) // Remove image if not checked
            holder.dEditExtraMuttonCost.isEnabled  = false
        }

        if (dayMealStatus.disAutoMeal == true) {
            holder.dBtnIsAutoMeal.setImageResource(R.drawable.ic_tikmark) // set auto meal image

        } else {
            holder.dBtnIsAutoMeal.setImageDrawable(null) // Remove image if not checked
        }

        // Set the meal costs for dinner
        holder.dEditMealCost.setText(dayMealStatus.dmealCost?.toString() ?: "")
        holder.dEditFuelCost.setText(dayMealStatus.dfuelCost?.toString() ?: "")
        holder.dEditExtraMuttonCost.setText(dayMealStatus.dextraMuttonCost?.toString() ?: "")


        holder.edit.setOnClickListener{
            var intent = Intent(context,DayStatusUpdateActivity::class.java)

            intent.putExtra("day",dayMealStatus.date)
            intent.putExtra("month",dayMealStatus.month)

            context.startActivity(intent)


        }



    }




    override fun getItemCount(): Int = dayMealStatusList.size
}
