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

class StudentRequestAdapter(
    private val studentList: ArrayList<Student>,
    private val message: String
) : RecyclerView.Adapter<StudentRequestAdapter.StudentRequestViewHolder>() {

    class StudentRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val room: TextView = itemView.findViewById(R.id.room)
        val id: TextView = itemView.findViewById(R.id.id)
        val name: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_request, parent, false)
        return StudentRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentRequestViewHolder, position: Int) {
        val student = studentList[position]
        holder.room.text = "${student.roomNo}"
        holder.id.text = "${student.studentId}"
        holder.name.text = student.name

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
                putExtra("department", student.dept)
                putExtra("batch", student.batch)
                putExtra("roomNo", student.roomNo)
                putExtra("isCommitteeMember", student.isCommitteeMember)
                putExtra("dueAmount", student.dueAmount)
                putExtra("key", student.key)
                putExtra("profilePic", student.profilePic)
                putExtra("password", student.password)
                putExtra("message", message)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = studentList.size
}
