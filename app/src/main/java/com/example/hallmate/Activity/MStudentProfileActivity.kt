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
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMstudentProfileBinding
import com.google.android.gms.tasks.Tasks
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
     var isLock :Boolean = false
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
            binding.lock.visibility = View.GONE
        }


        binding.back.setOnClickListener{
            finish()
        }





        binding.denyBtn.setOnClickListener{
            showConfirmationDialog1()
        }

        binding.acceptBtn.setOnClickListener{
            load.start()
            acceptStudent()
        }

        binding.save.setOnClickListener{
            load.start()
            updateStudent()
        }

        binding.delete.setOnClickListener{
            showConfirmationDialog2()
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
        updates["isLock"] = binding.lock.isChecked


        // Update only the changed fields
        studentRef.child(hallId).updateChildren(updates).addOnSuccessListener {

            load.end()
            successDialog.show("Updated","Successfully updated student data.",true,"")
        }.addOnFailureListener { error ->
            load.end()
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
        val hallIdEmailRef = database.getReference("HallIdEmail")

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

                                    val hallIdEmail = HallIdEmail(hallId,email)

                                    // Save the student data in the "Student" node
                                    studentRef.child(hallId).setValue(student)
                                        .addOnSuccessListener {
                                                    // Create the user account in Firebase Authentication
                                                    auth.createUserWithEmailAndPassword(email, password)
                                                        .addOnSuccessListener {

                                                            hallIdEmailRef.child(hallId).setValue(hallIdEmail)
                                                                .addOnSuccessListener {

                                                                            startJourney(hallId,name,studentId)

                                                                }
                                                                .addOnFailureListener {
                                                                    Toast.makeText(
                                                                        this@MStudentProfileActivity, "2 Failed to accept request.", Toast.LENGTH_SHORT).show()
                                                                }





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



    private fun startJourney(hallId: String, name: String, studentId: String) {
        val mealRef = FirebaseDatabase.getInstance().getReference("Meal")

        var feb = "02-2025"
        var mar = "03-2025"
        var apr = "04-2025"
        var may = "05-2025"

        //For february


        for(i in 1..4){
            var month:String = feb
            var limit :Int = 0
            when (i){
                1->{
                    month = feb
                    limit = 28
                }
                2->{
                    month = mar
                    limit = 31
                }
                3->{
                    month = apr
                    limit = 30
                }
                4->{
                    month = may
                    limit = 31
                }
            }


            for(d in 1..limit){
                var day = d.toString()
                if(d<10)
                    day = "0"+day


                var meal = Meal(month,day,hallId,false,
                    false,false,false,
                    false,false,false,
                    false,false,false)



                mealRef.child(month).child(hallId).child(day).setValue(meal)
                    .addOnSuccessListener {
                        if (i == 4 && d == 31) {

                            load.end()
                            successDialog.show("Accepted", "Name: $name\nStudent Id: $studentId\nHall Id: $hallId", false, "")

                        }
                    }
                    .addOnFailureListener {
                        // Handle failure
                        println("Failed to update meals: ${it.message}")
                    }
            }


        }







    }


    private fun showConfirmationDialog1() {

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

    private fun showConfirmationDialog2() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirmation)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val textTitle = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val btnPositive = dialog.findViewById<TextView>(R.id.positiveBtn)
        val btnNegative = dialog.findViewById<TextView>(R.id.negativeBtn)

        textTitle.setText("Delete Student?")
        textMessage.setText("Are you sure? This cannot be undone.")
        btnPositive.setText("Delete")

        btnNegative.setOnClickListener{
            dialog.dismiss()
        }
        btnPositive.setOnClickListener{
            dialog.dismiss()
            load.start()
            deleteStudent()
        }
        dialog.show()
    }

    private fun deleteStudent() {
        val studentRef = FirebaseDatabase.getInstance().getReference("Student")
        val hallIdEmailRef = FirebaseDatabase.getInstance().getReference("HallIdEmail")

        studentRef.child(hallId).removeValue().addOnSuccessListener {
            // After deleting from the Student table, try deleting from HallIdEmail
            hallIdEmailRef.child(hallId).removeValue().addOnSuccessListener {
                // After deleting from HallIdEmail table, check the Mutton table
                val muttonRef = FirebaseDatabase.getInstance().getReference("Mutton")
                muttonRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(muttonSnapshot: DataSnapshot) {
                        if (muttonSnapshot.exists()) {
                            // Step 3: Delete from Mutton table if it exists
                            muttonRef.child(hallId).removeValue().addOnSuccessListener {
                                // All deletions successful
                                load.end()
                                successDialog.show("Deleted","Student deleted successfully",true,"")
                            }.addOnFailureListener {
                                // Failed to delete from Mutton table, still proceed with other deletions
                                load.end()
                                successDialog.show("Deleted","Student deleted successfully",true,"")
                            }
                        } else {
                            // If Mutton table doesn't exist for this hallId, skip deletion in Mutton
                            load.end()
                            successDialog.show("Deleted","Student deleted successfully",true,"")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        load.end()
                        // Handle error in Mutton table check
                        Toast.makeText(this@MStudentProfileActivity, "Failed to check Mutton table for hallId.", Toast.LENGTH_SHORT).show()
                    }
                })
            }.addOnFailureListener {
                load.end()
                // Failed to delete from HallIdEmail table, show error message
                Toast.makeText(this@MStudentProfileActivity, "Failed to delete from HallIdEmail table.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            load.end()
            // Failed to delete from Student table, show error message
            Toast.makeText(this@MStudentProfileActivity, "Failed to delete from Student table.", Toast.LENGTH_SHORT).show()
        }

    }//////





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
            binding.updateBtnLayout.visibility = View.VISIBLE
        }else if(message=="student_request"){
            binding.requestBtnLayout.visibility = View.VISIBLE
            binding.updateBtnLayout.visibility = View.GONE
        }
    }

    private fun setAllData() {
       binding.name.setText(name)
        binding.hallId.setText("Hall Id: "+hallId)
        binding.studentId.setText(studentId)
        binding.room.setText(roomNo)
        binding.department.setText(department)
        binding.batch.setText(batch)
        binding.email.setText(email)
        binding.phone.setText(phone)
        binding.lock.isChecked = isLock



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
        isLock = intent.getBooleanExtra("isLock",false)
        message = intent.getStringExtra("message").toString()
    }
}