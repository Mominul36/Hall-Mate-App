package com.example.hallmate.Activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.MainActivity
import com.example.hallmate.Model.Mutton
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityManagerHomeBinding
import com.example.hallmate.databinding.ActivityMprofileBinding
import com.example.hallmate.databinding.ActivitySstudentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SStudentProfileActivity : AppCompatActivity(), DialogDismissListener {

    lateinit var hallId :String
    lateinit var studentId :String
    lateinit var name :String
    lateinit var email:String
    lateinit var phone :String
    lateinit var department :String
    lateinit var batch :String
    lateinit var roomNo :String
    var isCommitteeMember : Boolean?=null
    lateinit var key :String
    lateinit var password :String
     var isMutton :Boolean = false
    var isLock :Boolean = false


    lateinit var binding: ActivitySstudentProfileBinding
    lateinit var auth : FirebaseAuth
    var database = FirebaseDatabase.getInstance()
    lateinit var successDialog: SuccessDialog

    lateinit var load:Loading

    var flagOldPass = false
    var flagNewPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySstudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)
        successDialog = SuccessDialog(this,this)

        auth = FirebaseAuth.getInstance()


        setUserDetails()

        binding.back.setOnClickListener{
            finish()
        }

        binding.details.setOnClickListener {
            showDetailsLayout()
        }
        binding.password.setOnClickListener {
            showPasswordLayout()
        }
        binding.passchange.setOnClickListener{
            makeChangePassword()
        }

        binding.oldHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.newHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.oldHiddenIcon.setOnClickListener {
            if (flagOldPass) {
                binding.oldpass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.oldHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.oldpass.transformationMethod = null
                binding.oldHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.oldpass.setSelection(binding.oldpass.text.length)
            flagOldPass = !flagOldPass
        }
        binding.newHiddenIcon.setOnClickListener{
            if (flagNewPass) {
                binding.newpass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.newHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.newpass.transformationMethod = null
                binding.newHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.newpass.setSelection(binding.newpass.text.length)
            flagNewPass = !flagNewPass
        }



        binding.mutton.setOnCheckedChangeListener { _, isChecked ->
            load.start()
            var databaseRef = database.getReference("Student")

            val updates = hashMapOf<String, Any>(
                "isMutton" to isChecked
            )
            databaseRef.child(hallId).updateChildren(updates)
                .addOnSuccessListener {
                    if (isChecked) {
                        saveToMutton()
                    } else {
                        deleteFromMutton()
                    }
                }
                .addOnFailureListener { exception ->
                    load.end()
                    Toast.makeText(this, "Error to change Mutton Meal Status: ${exception.message}", Toast.LENGTH_SHORT).show()
                }






        }








    }




    private fun saveToMutton() {
        val muttonRef = FirebaseDatabase.getInstance().getReference("Mutton")

        // Check if hallId already exists in Mutton table
        muttonRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // hallId does not exist, so add it
                    val mutton = Mutton(hallId)
                    muttonRef.child(hallId).setValue(mutton)
                        .addOnSuccessListener {

                            load.end()
                            successDialog.show("Success","Mutton meal Active.",true,"")

                        }
                        .addOnFailureListener { exception ->
                            load.end()
                            Toast.makeText(this@SStudentProfileActivity, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    load.end()
                    successDialog.show("Success","Mutton meal Active.",true,"")
                    // Toast.makeText(this@MStaffProfileActivity, "Hall ID already exists in Mutton", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                load.end()
                Toast.makeText(this@SStudentProfileActivity, "Internet error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFromMutton() {
        val muttonRef = FirebaseDatabase.getInstance().getReference("Mutton")

        // Check if hallId exists before deleting
        muttonRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // hallId exists, so delete it
                    muttonRef.child(hallId).removeValue()
                        .addOnSuccessListener {
                            load.end()
                            successDialog.show("Success","Mutton meal Inactive.",true,"")

                        }
                        .addOnFailureListener { exception ->
                            load.end()
                            Toast.makeText(this@SStudentProfileActivity, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    load.end()
                    successDialog.show("Success","Mutton meal Inactive.",true,"")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                load.end()
                Toast.makeText(this@SStudentProfileActivity, "Network Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun makeChangePassword() {
        load.start()
        val oldPass = binding.oldpass.text.toString().trim()
        val newPass = binding.newpass.text.toString().trim()

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            load.end()
            Toast.makeText(this, "Enter both old and new passwords", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            load.end()
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val email = user.email
        if (email.isNullOrEmpty()) {
            load.end()
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(email, oldPass)

        // Reauthenticate the user before changing the password
        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Change the password
                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        binding.oldpass.setText("")
                        binding.newpass.setText("")

                        updatePasswordInDatabase(hallId,newPass)

                    } else {
                        load.end()
                        Toast.makeText(this, "Password must be at least 6 character", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                load.end()
                Toast.makeText(this, "Incorrect Old Password ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePasswordInDatabase(hallId: String, newPass: String) {
    var databaseRef = database.getReference("Student")

        val updates = hashMapOf<String, Any>(
            "password" to newPass
        )

        databaseRef.child(hallId).updateChildren(updates)
            .addOnSuccessListener {
                load.end()
               // Toast.makeText(this, "Password Changed successfully", Toast.LENGTH_SHORT).show()

                var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("password", newPass)
                editor.apply()

                successDialog.show("Success","Password Changed successfully",true,"")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error updating password: ${exception.message}", Toast.LENGTH_SHORT).show()
            }




    }


    private fun setUserDetails() {
        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)

        hallId = sharedPreferences.getString("hallId", "").toString()
        studentId = sharedPreferences.getString("studentId", "").toString()
        name = sharedPreferences.getString("name", "").toString()
        email = sharedPreferences.getString("email", "").toString()
        phone = sharedPreferences.getString("phone", "").toString()
        department = sharedPreferences.getString("department", "").toString()
        batch = sharedPreferences.getString("batch", "").toString()
        roomNo = sharedPreferences.getString("roomNo", "").toString()
        isCommitteeMember = sharedPreferences.getBoolean("isCommitteeMember",false)
        key = sharedPreferences.getString("key", "").toString()
        password = sharedPreferences.getString("password", "").toString()
        isMutton = sharedPreferences.getBoolean("isMutton",false)


        binding.name.setText(name)
        binding.hallId.setText("Hall Id: "+hallId)
        binding.studentId.setText(studentId)
        binding.email.setText(email)
        binding.phone.setText(phone)
        binding.department.setText(department)
        binding.batch.setText(batch)
        binding.room.setText(roomNo)
        binding.mutton.isChecked = isMutton

    }


    private fun showDetailsLayout() {
        binding.details.setBackgroundColor(ContextCompat.getColor(this, R.color.base_color))
        binding.details.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.password.setBackgroundColor(ContextCompat.getColor(this, R.color.temp_color))
        binding.password.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.mainLayout.visibility = View.VISIBLE
        binding.passwordLayout.visibility = View.GONE
    }

    private fun showPasswordLayout() {
        binding.password.setBackgroundColor(ContextCompat.getColor(this, R.color.base_color))
        binding.password.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.details.setBackgroundColor(ContextCompat.getColor(this, R.color.temp_color))
        binding.details.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.mainLayout.visibility = View.GONE
        binding.passwordLayout.visibility = View.VISIBLE
    }

    override fun onDialogDismissed(message: String) {


    }
}