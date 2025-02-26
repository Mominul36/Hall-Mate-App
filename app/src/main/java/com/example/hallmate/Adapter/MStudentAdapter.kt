package com.example.hallmate.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallmate.Activity.MStudentProfileActivity
import com.example.hallmate.Model.Student
import com.example.hallmate.R

class MStudentAdapter(
    private val studentList: ArrayList<Student>,
) : RecyclerView.Adapter<MStudentAdapter.MStudentViewHolder>() {

    class MStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val room: TextView = itemView.findViewById(R.id.room)
        val id: TextView = itemView.findViewById(R.id.id)
        val name: TextView = itemView.findViewById(R.id.name)
        val hallId: TextView = itemView.findViewById(R.id.hallId)
        val dept: TextView = itemView.findViewById(R.id.dept)
        val batch: TextView = itemView.findViewById(R.id.batch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MStudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return MStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MStudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.room.text = "${student.roomNo}"
        holder.id.text = "${student.studentId}"
        holder.name.text = student.name
        holder.hallId.text = student.hallId
        holder.dept.text = student.department
        holder.batch.text = student.batch

        // Set onClickListener to navigate to the student detail activity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MStudentProfileActivity::class.java).apply {
                // Pass all student details
                putExtra("hallId", student.hallId)
                putExtra("studentId", student.studentId)
                putExtra("name", student.name)
                putExtra("email", student.email)
                putExtra("phone", student.phone)
                putExtra("department", student.department)
                putExtra("batch", student.batch)
                putExtra("roomNo", student.roomNo)
                putExtra("isCommitteeMember", student.isCommitteeMember)
                putExtra("dueAmount", student.dueAmount)
                putExtra("key", student.key)
                putExtra("profilePic", student.profilePic)
                putExtra("password", student.password)
                putExtra("mealCode", student.mealCode)
                putExtra("isLock", student.isLock)
                putExtra("message", "current_student")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = studentList.size
}
