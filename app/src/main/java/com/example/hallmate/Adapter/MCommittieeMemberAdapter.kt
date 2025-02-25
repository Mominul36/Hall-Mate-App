package com.example.hallmate.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hallmate.Activity.MStudentProfileActivity
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.google.firebase.database.FirebaseDatabase

class MCommittieeMemberAdapter(
    private val context: Context,
    private var load: Loading,
    private val successDialog: SuccessDialog,
    private val studentList: ArrayList<Student>,
) : RecyclerView.Adapter<MCommittieeMemberAdapter.MCommittieeMemberViewHolder>() {

    class MCommittieeMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.id)
        val name: TextView = itemView.findViewById(R.id.name)
        val hallId: TextView = itemView.findViewById(R.id.hallId)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MCommittieeMemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_commettie_member, parent, false)
        return MCommittieeMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MCommittieeMemberViewHolder, position: Int) {
        val student = studentList[position]
        holder.id.text = "${student.studentId}"
        holder.name.text = student.name
        holder.hallId.text = student.hallId

        holder.delete.setOnClickListener {
            // Show confirmation dialog before removing the student
            showConfirmationDialog(student, position)
        }

        // Set onClickListener to navigate to the student detail activity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MStudentProfileActivity::class.java).apply {
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
                putExtra("message", "current_student")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = studentList.size

    private fun showConfirmationDialog(student: Student, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirmation)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val textTitle = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val btnPositive = dialog.findViewById<TextView>(R.id.positiveBtn)
        val btnNegative = dialog.findViewById<TextView>(R.id.negativeBtn)

        textTitle.text = "Are you sure?"
        textMessage.text = "Are you sure? This cannot be undone."
        btnPositive.text = "Remove"

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }

        btnPositive.setOnClickListener {
            // Remove the student from the list and update the database
            removeStudentFromCommittee(student, position)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun removeStudentFromCommittee(student: Student, position: Int) {
        // Assuming you have a reference to the Firebase database
        val studentRef = FirebaseDatabase.getInstance().getReference("Student")

        // Create a map to update the isCommitteeMember field
        val updates = mapOf<String, Any>(
            "isCommitteeMember" to false
        )

        // Update the specific field without overwriting the entire student data
        studentRef.child(student.hallId!!).updateChildren(updates)
            .addOnSuccessListener {

                


                load.end()
                successDialog.show("Removed", "${student.name} removed from Food committee!", true, "")
            }
            .addOnFailureListener {
                load.end()
                Toast.makeText(context, "Failed to remove student.", Toast.LENGTH_SHORT).show()
            }
    }

}
