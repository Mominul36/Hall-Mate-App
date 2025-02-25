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
import com.example.hallmate.Model.StudentHallBill
import com.example.hallmate.R

class SStudentHallBillAdapter(
    private val context: Context,
    private val hallBillList: ArrayList<StudentHallBill>,
) : RecyclerView.Adapter<SStudentHallBillAdapter.SStudentHallBillViewHolder>() {



    class SStudentHallBillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val costB: TextView = itemView.findViewById(R.id.costB)
        val costL: TextView = itemView.findViewById(R.id.costL)
        val costD: TextView = itemView.findViewById(R.id.costD)
        val costT: TextView = itemView.findViewById(R.id.costT)


        val crosB: ImageView = itemView.findViewById(R.id.crosB)
        val crosL: ImageView = itemView.findViewById(R.id.crosL)
        val crosD: ImageView = itemView.findViewById(R.id.crosD)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SStudentHallBillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_hallbil, parent, false)
        return SStudentHallBillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SStudentHallBillViewHolder, position: Int) {
        val studentHallBill = hallBillList[position]

        holder.txtDate.text = studentHallBill.date.toString()

        if(studentHallBill.bisShowing==false){
            holder.crosB.visibility = View.GONE
            holder.costB.visibility = View.GONE
        }else{
            if(studentHallBill.bstatus==false){
                holder.costB.visibility = View.GONE
            }else{
                holder.crosB.visibility = View.GONE
                holder.costB.setText(studentHallBill.bCost.toString())
            }

        }


        if(studentHallBill.lisShowing==false){
            holder.crosL.visibility = View.GONE
            holder.costL.visibility = View.GONE
        }else{
            if(studentHallBill.lstatus==false){
                holder.costL.visibility = View.GONE
            }else{
                holder.crosL.visibility = View.GONE
                holder.costL.setText(studentHallBill.lCost.toString())
            }

        }


        if(studentHallBill.disShowing==false){
            holder.crosD.visibility = View.GONE
            holder.costD.visibility = View.GONE
        }else{
            if(studentHallBill.dstatus==false){
                holder.costD.visibility = View.GONE
            }else{
                holder.crosD.visibility = View.GONE
                holder.costD.setText(studentHallBill.dCost.toString())
            }

        }


        if(studentHallBill.bisShowing==false && studentHallBill.lisShowing==false && studentHallBill.disShowing==false){
            holder.costT.visibility = View.GONE
        }




    }

    override fun getItemCount(): Int = hallBillList.size



}
