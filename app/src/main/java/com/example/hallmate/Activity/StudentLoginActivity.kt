package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMainBinding
import com.example.hallmate.databinding.ActivityStudentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentLoginActivity : AppCompatActivity(), DialogDismissListener {
    lateinit var binding: ActivityStudentLoginBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    lateinit var load : Loading
    lateinit var success: SuccessDialog

    var flagPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)
        success= SuccessDialog(this,this)

        binding.goToRegister.setOnClickListener{
            startActivity(Intent(this, StudentRegistrationActivity::class.java))
            finish()
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


        binding.login.setOnClickListener{
            login()
        }






    }

    private fun login() {
        load.start()
       val hallId = binding.hallId.text.toString()
        val password = binding.password.text.toString()


        val databaseRef = database.getReference("Student")


        databaseRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val student = snapshot.getValue(Student::class.java)
                    if(student!=null){
                        signIn(student,password)
                    }
                } else {
                    load.end()
                    Toast.makeText(this@StudentLoginActivity, "You do not have any account.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                load.end()
                Toast.makeText(this@StudentLoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })









    }

    override fun onDialogDismissed(message: String) {
        TODO("Not yet implemented")
    }

    private fun signIn(student: Student, password: String) {
        auth.signInWithEmailAndPassword(student.email.toString(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear().apply()


                    editor.putString("hallId", student.hallId)
                    editor.putString("studentId", student.studentId)
                    editor.putString("name", student.name)
                    editor.putString("email", student.email)
                    editor.putString("phone", student.phone)
                    editor.putString("department", student.department)
                    editor.putString("batch", student.batch)
                    editor.putString("roomNo", student.roomNo)
                    editor.putBoolean("isCommitteeMember", student.isCommitteeMember ?: false)
                    editor.putFloat("dueAmount", student.dueAmount?.toFloat() ?: 0.0f)
                    editor.putString("key", student.key)
                    editor.putString("profilePic", student.profilePic)
                    editor.putString("password", student.password)
                    editor.putString("mealCode", student.mealCode)
                    editor.putBoolean("isLock", student.isLock ?: false)
                    editor.putBoolean("isMutton", student.isMutton ?: false)

                    editor.apply()


                      load.end()
                        val intent = Intent(this, StudentHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)



//                    var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
//                    val editor = sharedPreferences.edit()
//
//                    editor.putString("name",user.name)
//                    editor.putString("email",user.email)
//                    editor.putString("phone",user.phone)
//                    editor.putString("designation",user.designation)
//                    editor.putString("userType",user.userType)
//                    editor.putString("profilePic",user.profilePic)
//                    editor.apply()

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
            }.addOnFailureListener{exception ->
                load.end()
               // Toast.makeText(this, "Sign-in failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }




    }
}