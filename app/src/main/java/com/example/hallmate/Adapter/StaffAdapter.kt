package com.example.hallmate.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallmate.Activity.MStaffProfileActivity
import com.example.hallmate.Activity.MStudentProfileActivity
import com.example.hallmate.Model.Student
import com.example.hallmate.R

class StaffAdapter(
    private val staffList: ArrayList<Student>,
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    class StaffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hallId: TextView = itemView.findViewById(R.id.hallId)
        val name: TextView = itemView.findViewById(R.id.name)
        val position: TextView = itemView.findViewById(R.id.position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]

        holder.hallId.text = "${staff.hallId}"
        holder.name.text = staff.name
        holder.position.text = staff.department

        // Set onClickListener to navigate to the student detail activity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MStaffProfileActivity::class.java).apply {
                // Pass all student details
                putExtra("hallId", staff.hallId)
                putExtra("name", staff.name)
                putExtra("department", staff.department)
                putExtra("key", staff.key)
                putExtra("isMutton", staff.isMutton)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = staffList.size
}
