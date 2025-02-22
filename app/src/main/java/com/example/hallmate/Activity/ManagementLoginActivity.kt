package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.Management
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityManagementLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManagementLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagementLoginBinding
    private lateinit var auth: FirebaseAuth

    lateinit var load : Loading

    var flagPass = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityManagementLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)
        auth = FirebaseAuth.getInstance()

        binding.login.setOnClickListener {
            checkUserExists()
        }


        binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.passHiddenIcon.setOnClickListener {
            if (flagPass) {
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.password.transformationMethod = null
                binding.passHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.password.setSelection(binding.password.text.length)
            flagPass = !flagPass
        }





    }


    private fun checkUserExists(){

        val databaseRef = FirebaseDatabase.getInstance().getReference("Management")
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val key = email.substringBefore("@")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        load.start()
        databaseRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User found,process to login
                    var user = snapshot.getValue(Management::class.java)
                    if(user!=null){
                        login(user,email,password)
                    }
                } else {
                    // User not found, show an error message
                    load.end()
                    Toast.makeText(this@ManagementLoginActivity, "User not found!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database errors
                load.end()
                Toast.makeText(this@ManagementLoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun login(user:Management,email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    load.end()
                    if(user.userType=="1"){
                        val intent = Intent(this, ProvostHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }else{
                        val intent = Intent(this, ManagerHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }


                    var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.putString("name",user.name)
                    editor.putString("email",user.email)
                    editor.putString("phone",user.phone)
                    editor.putString("designation",user.designation)
                    editor.putString("userType",user.userType)
                    editor.putString("profilePic",user.profilePic)
                    editor.apply()

                    finish()
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "User not found!"
                        is FirebaseAuthInvalidCredentialsException -> "Wrong password"
                        else -> task.exception?.message ?: "Login failed"
                    }
                    load.end()
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

}



