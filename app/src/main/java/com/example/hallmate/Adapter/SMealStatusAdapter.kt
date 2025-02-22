package com.example.hallmate.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hallmate.Class.Loading
import com.example.hallmate.Model.DayMealStatusForRecycler
import com.example.hallmate.Model.MealForRV
import com.example.hallmate.R

class SMealStatusAdapter(
    private val context: Context,
    private val mealForRVList: ArrayList<MealForRV>,
) : RecyclerView.Adapter<SMealStatusAdapter.MealStatusViewHolder>() {

    var  bStatus: Boolean = false
    var  lStatus: Boolean = false
    var  dStatus: Boolean = false
    var  sStatus: Boolean = false





    class MealStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val bBtnStatus: ImageView = itemView.findViewById(R.id.bBtnStatus)
        val lBtnStatus: ImageView = itemView.findViewById(R.id.lBtnStatus)
        val dBtnStatus: ImageView = itemView.findViewById(R.id.dBtnStatus)
        val sBtnStatus: ImageView = itemView.findViewById(R.id.sBtnStatus)

        val bItemStatus: LinearLayout = itemView.findViewById(R.id.bItemStatus)
        val lItemStatus: LinearLayout = itemView.findViewById(R.id.lItemStatus)
        val dItemStatus: LinearLayout = itemView.findViewById(R.id.dItemStatus)
        val sItemStatus: LinearLayout = itemView.findViewById(R.id.sItemStatus)








    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealStatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_meal_status, parent, false)
        return MealStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealStatusViewHolder, position: Int) {
        val mealForRV = mealForRVList[position]


        holder.txtDate.text = mealForRV.date

        if(mealForRV.isRamadan==false){
            holder.sItemStatus.visibility= View.GONE
        }

        if (mealForRV.bstatus == true) {
            holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
            bStatus = true
        } else {
            holder.bBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (mealForRV.dstatus == true) {
            holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)
            dStatus = true
        } else {
            holder.dBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (mealForRV.lstatus == true) {
            if(mealForRV.isSahari==true){
                holder.sBtnStatus.setImageResource(R.drawable.ic_tikmark)
                holder.lBtnStatus.setImageDrawable(null)
                sStatus = true
            }else{
                holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
                holder.sBtnStatus.setImageDrawable(null)
                lStatus = true
            }

        } else {
            holder.lBtnStatus.setImageDrawable(null)
            holder.sBtnStatus.setImageDrawable(null)
        }



        holder.bItemStatus.setOnClickListener{
            if(mealForRV.bstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.bIsAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{

                    if(bStatus==true){
                        holder.bBtnStatus.setImageDrawable(null)
                        bStatus = !bStatus
                    }else{
                        holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        bStatus = !bStatus
                    }

                }
            }
        }


        holder.dItemStatus.setOnClickListener{
            if(mealForRV.dstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.dIsAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{
                    if(dStatus==true){
                        holder.dBtnStatus.setImageDrawable(null)
                        dStatus = !dStatus
                    }else{
                        holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        dStatus = !dStatus
                    }
                }
            }
        }


        holder.lItemStatus.setOnClickListener{
            if(mealForRV.lstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.lIsAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{
                    if(lStatus==true){
                        holder.lBtnStatus.setImageDrawable(null)
                        lStatus = !lStatus
                    }else{
                        holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        lStatus = !lStatus

                        holder.sBtnStatus.setImageDrawable(null)
                        sStatus = false
                    }

                }
            }
        }


        holder.sItemStatus.setOnClickListener{

            if(mealForRV.lstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.lIsAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{
                    if(sStatus==true){
                        holder.sBtnStatus.setImageDrawable(null)
                        sStatus = !sStatus
                    }else{
                        holder.sBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        sStatus = !sStatus

                        holder.lBtnStatus.setImageDrawable(null)
                        lStatus = false
                    }


                }
            }
        }












    }




    override fun getItemCount(): Int = mealForRVList.size
}
