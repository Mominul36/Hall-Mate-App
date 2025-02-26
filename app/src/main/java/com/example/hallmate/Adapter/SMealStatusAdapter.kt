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
import com.example.hallmate.Model.MealForRV
import com.example.hallmate.R

class SMealStatusAdapter(
    private val context: Context,
    private val mealForRVList: ArrayList<MealForRV>,
) : RecyclerView.Adapter<SMealStatusAdapter.MealStatusViewHolder>() {



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




        if (mealForRV.bstatus == true) {
            holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
        } else {
            holder.bBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (mealForRV.dstatus == true) {
            holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)
        } else {
            holder.dBtnStatus.setImageDrawable(null) // Remove image if not checked
        }

        if (mealForRV.lstatus == true) {
            if(mealForRV.isSahari==true){
                holder.sBtnStatus.setImageResource(R.drawable.ic_tikmark)
                holder.lBtnStatus.setImageDrawable(null)
            }else{
                holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
                holder.sBtnStatus.setImageDrawable(null)
            }

        } else {
            holder.lBtnStatus.setImageDrawable(null)
            holder.sBtnStatus.setImageDrawable(null)
        }



        holder.bItemStatus.setOnClickListener{
            if(mealForRV.bstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.bisAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{

                    if(mealForRV.bstatus==true){
                        holder.bBtnStatus.setImageDrawable(null)
                        mealForRV.bstatus = !mealForRV.bstatus!!
                        notifyItemChanged(position)
                    }else{
                        holder.bBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        mealForRV.bstatus = !mealForRV.bstatus!!
                        notifyItemChanged(position)
                    }

                }
            }
        }


        holder.dItemStatus.setOnClickListener{
            if(mealForRV.dstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.disAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{
                    if(mealForRV.dstatus==true){
                        holder.dBtnStatus.setImageDrawable(null)
                        mealForRV.dstatus = !mealForRV.dstatus!!
                        notifyItemChanged(position)
                    }else{
                        holder.dBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        mealForRV.dstatus = !mealForRV.dstatus!!
                        notifyItemChanged(position)
                    }
                }
            }
        }


        holder.lItemStatus.setOnClickListener{
            if(mealForRV.lstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.lisAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{
                    if(mealForRV.lstatus==false){
                        holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        mealForRV.lstatus = true
                        mealForRV.isSahari = false
                        notifyItemChanged(position)
                    }else{
                     if( mealForRV.isSahari==false){
                         holder.lBtnStatus.setImageDrawable(null)
                         mealForRV.lstatus = false
                         mealForRV.isSahari = false
                         notifyItemChanged(position)
                     }else{
                         holder.lBtnStatus.setImageResource(R.drawable.ic_tikmark)
                         holder.sBtnStatus.setImageDrawable(null)
                         mealForRV.lstatus = true
                         mealForRV.isSahari = false
                         notifyItemChanged(position)
                     }

                    }
                }
            }
        }



        holder.sItemStatus.setOnClickListener{
            if(mealForRV.lstatusDay==false){
                Toast.makeText(context,"Meal not available at this time",Toast.LENGTH_SHORT).show()
            }else{
                if(mealForRV.lisAutoMeal==true){
                    Toast.makeText(context,"Auto Meal",Toast.LENGTH_SHORT).show()
                }else{
                    if(mealForRV.lstatus==false){
                        holder.sBtnStatus.setImageResource(R.drawable.ic_tikmark)
                        mealForRV.lstatus = true
                        mealForRV.isSahari = true
                        notifyItemChanged(position)
                    }else{
                        if( mealForRV.isSahari==false){
                            holder.sBtnStatus.setImageResource(R.drawable.ic_tikmark)
                            holder.lBtnStatus.setImageDrawable(null)
                            mealForRV.lstatus = true
                            mealForRV.isSahari = true
                            notifyItemChanged(position)
                        }else{
                            holder.sBtnStatus.setImageDrawable(null)
                            mealForRV.lstatus = false
                            mealForRV.isSahari = false
                            notifyItemChanged(position)
                        }

                    }
                }
            }
        }












    }




    override fun getItemCount(): Int = mealForRVList.size

    fun getUpdatedMealData(): List<MealForRV> {
        return mealForRVList
    }


}
