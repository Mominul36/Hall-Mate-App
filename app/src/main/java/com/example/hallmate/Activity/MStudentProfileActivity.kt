package com.example.hallmate.Activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMstudentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MStudentProfileActivity : AppCompatActivity(), DialogDismissListener {
    lateinit var binding : ActivityMstudentProfileBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()


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
    lateinit var message :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMstudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        getAllData()
        setButtonStatus()
        setAllData()

        binding.back.setOnClickListener{
            finish()
        }

        val successDialog = SuccessDialog(this, "Success", "Your operation was successful", this)
       // successDialog.show()













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

    override fun onDialogDismissed() {
        // Do something when the dialog is dismissed
        Toast.makeText(this, "Dialog was dismissed", Toast.LENGTH_SHORT).show()
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
        message = intent.getStringExtra("message").toString()
    }
}