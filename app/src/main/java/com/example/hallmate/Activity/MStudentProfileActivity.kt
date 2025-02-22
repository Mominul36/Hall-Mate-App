package com.example.hallmate.Activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.Hall
import com.example.hallmate.Model.HallIdEmail
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMstudentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class MStudentProfileActivity : AppCompatActivity(), DialogDismissListener {
    lateinit var binding : ActivityMstudentProfileBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    lateinit var load :Loading
    lateinit var successDialog: SuccessDialog

    lateinit var hallId :String
    lateinit var studentId :String
    lateinit var name :String
    lateinit var email:String
    lateinit var phone :String
    lateinit var department :String
    lateinit var batch :String
    lateinit var roomNo :String
     var isCommitteeMember : Boolean?=null
     var dueAmount : Double?=null
    lateinit var profilePic :String
    lateinit var key :String
    lateinit var password :String
    lateinit var mealCode :String
    lateinit var message :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMstudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        load = Loading(this)
        successDialog = SuccessDialog(this, this)

        getAllData()
        setButtonStatus()
        setAllData()
        if(message=="student_request"){
            binding.dueAmountLayout.visibility = View.GONE
        }

        binding.back.setOnClickListener{
            finish()
        }







        binding.denyBtn.setOnClickListener{
            showConfirmationDialog()
        }

        binding.acceptBtn.setOnClickListener{
            load.start()
            acceptStudent()
        }

        binding.saveBtn.setOnClickListener{
            load.start()
            updateStudent()
        }










        binding.name.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.name.setBackgroundResource(R.drawable.edit_focus_background)
                binding.name.setPadding(20,10,20,10)
            } else {
                binding.name.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.name.setPadding(0,0,0,0)
            }
        }

        binding.room.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.room.setBackgroundResource(R.drawable.edit_focus_background)
                binding.room.setPadding(20,10,20,10)
            } else {
                binding.room.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.room.setPadding(0,0,0,0)
            }
        }

        binding.dueAmount.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.dueAmount.setBackgroundResource(R.drawable.edit_focus_background)
                binding.dueAmount.setPadding(20,10,20,10)
            } else {
                binding.dueAmount.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.dueAmount.setPadding(0,0,0,0)
            }
        }

        binding.department.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.department.setBackgroundResource(R.drawable.edit_focus_background)
                binding.department.setPadding(20,10,20,10)
            } else {
                binding.department.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.department.setPadding(0,0,0,0)
            }
        }

        binding.batch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.batch.setBackgroundResource(R.drawable.edit_focus_background)
                binding.batch.setPadding(20,10,20,10)
            } else {
                binding.batch.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.batch.setPadding(0,0,0,0)
            }
        }















    }

    private fun updateStudent() {
        val studentRef = database.getReference("Student")

        // Prepare updated fields
        val updates = mutableMapOf<String, Any?>()
        updates["name"] = binding.name.text.toString()
        updates["roomNo"] = binding.room.text.toString()
        updates["department"] = binding.department.text.toString()
        updates["batch"] = binding.batch.text.toString()

        // Ensure 'dueAmount' is a valid number before updating
        val dueAmountStr = binding.dueAmount.text.toString()

        if (dueAmountStr.isNotEmpty()) {
            val dueAmountDouble = dueAmountStr.toDoubleOrNull()
            if (dueAmountDouble != null) {
                updates["dueAmount"] = dueAmountDouble
            } else {
                // Check if it's a valid integer
                val dueAmountInt = dueAmountStr.toIntOrNull()
                if (dueAmountInt != null) {
                    updates["dueAmount"] = dueAmountInt.toDouble() // Convert integer to double if needed
                } else {
                    Toast.makeText(this, "Due amount is invalid", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }


        // Update only the changed fields
        studentRef.child(hallId).updateChildren(updates).addOnSuccessListener {
            load.end()
            successDialog.show("Updated","Successfully updated student data.",true,"")
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Failed to update: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun acceptStudent() {
        // Get the data from the UI
        name = binding.name.text.toString()
        roomNo = binding.room.text.toString()
        department = binding.department.text.toString()
        batch = binding.batch.text.toString()

        val studentRequestRef = database.getReference("Student_Request")
        val studentRef = database.getReference("Student")
        val hallRef = database.getReference("Hall")

        val key = UUID.randomUUID().toString()

        // Get the Hall data and update it
        hallRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val hall = snapshot.getValue(Hall::class.java)
                hall?.let {
                    // Get the lastHallId and increment it
                    hallId = hall.lastHallId.toString()
                    val id: Int = (hall.lastHallId?.toInt() ?: 0) + 1

                    val updates = hashMapOf<String, Any>(
                        "lastHallId" to id, // Update the lastHallId
                    )

                    // Log the update attempt
                   // Log.d("Hall Update", "Updating Hall with: $updates")

                    // Perform the update
                    hallRef.updateChildren(updates)
                        .addOnSuccessListener {
                            Log.d("Hall Update", "Successfully updated Hall")

                            // Remove the student request and process further
                            studentRequestRef.child(studentId).removeValue()
                                .addOnSuccessListener {
                                    val student = Student(
                                        hallId, studentId, name, email, phone, department, batch, roomNo, isCommitteeMember,
                                        dueAmount, key, profilePic, password, mealCode,false,false
                                    )
                                    val hallIdEmail = HallIdEmail(hallId, email)

                                    // Save the student data in the "Student" node
                                    studentRef.child(hallId).setValue(student)
                                        .addOnSuccessListener {
                                                    // Create the user account in Firebase Authentication
                                                    auth.createUserWithEmailAndPassword(email, password)
                                                        .addOnSuccessListener {
                                                            // Complete Acceptance
                                                            load.end()
                                                            successDialog.show("Accepted", "Name: $name\nStudent Id: $studentId\nHall Id: $hallId", false, "")
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(this@MStudentProfileActivity, "1 Failed to accept request.", Toast.LENGTH_SHORT).show()
                                                        }

                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this@MStudentProfileActivity, "2 Failed to accept request.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@MStudentProfileActivity, "3 Failed to accept request.", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { exception ->
                            // Log failure and show error message
                            Log.d("Hall Update", "Failed to update Hall: ${exception.message}")
                            Toast.makeText(this@MStudentProfileActivity, "Failed to accept request.", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // If no data is found in the Hall node
                Log.d("Hall Update", "No data available")
            }
        }.addOnFailureListener {
            // Log error if fetching Hall data failed
            Log.d("Hall Update", "Error: ${it.message}")
        }
    }


    private fun showConfirmationDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirmation)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val textTitle = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val btnPositive = dialog.findViewById<TextView>(R.id.positiveBtn)
        val btnNegative = dialog.findViewById<TextView>(R.id.negativeBtn)

        textTitle.setText("Deny Request?")
        textMessage.setText("Are you sure? This cannot be undone.")
        btnPositive.setText("Delete")

        btnNegative.setOnClickListener{
            dialog.dismiss()
        }
        btnPositive.setOnClickListener{
            dialog.dismiss()
            load.start()
            denyRequest()
        }
        dialog.show()
    }

    private fun denyRequest() {
       val databaseRef = database.getReference("Student_Request")
        databaseRef.child(studentId).removeValue()
            .addOnSuccessListener {
                successDialog.show("Denied", "Request denied successfully.",true,"")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to deny request.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDialogDismissed(ins:String) {
        finish()
    }

    private fun setButtonStatus() {
        if(message=="current_student" || message=="committee_member"){
             binding.requestBtnLayout.visibility = View.GONE
            binding.saveBtnLayout.visibility = View.VISIBLE
        }else if(message=="student_request"){
            binding.requestBtnLayout.visibility = View.VISIBLE
            binding.saveBtnLayout.visibility = View.GONE
        }
    }

    private fun setAllData() {
       binding.name.setText(name)
        binding.hallId.setText(hallId)
        binding.studentId.setText(studentId)
        binding.room.setText(roomNo)
        binding.dueAmount.setText(dueAmount.toString())
        binding.department.setText(department)
        binding.batch.setText(batch)
        binding.email.setText(email)
        binding.phone.setText(phone)



    }

    private fun getAllData() {
        hallId = intent.getStringExtra("hallId").toString()
        studentId = intent.getStringExtra("studentId").toString()
        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()
        phone = intent.getStringExtra("phone").toString()
        department = intent.getStringExtra("department").toString()
        batch = intent.getStringExtra("batch").toString()
        roomNo = intent.getStringExtra("roomNo").toString()
        isCommitteeMember = intent.getBooleanExtra("isCommitteeMember", false)
        dueAmount = intent.getDoubleExtra("dueAmount", 0.0)
        profilePic = intent.getStringExtra("profilePic").toString()
        key = intent.getStringExtra("key").toString()
        password = intent.getStringExtra("password").toString()
        mealCode = intent.getStringExtra("mealCode").toString()
        message = intent.getStringExtra("message").toString()
    }
}