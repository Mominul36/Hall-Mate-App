package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.MainActivity
import com.example.hallmate.Model.Management
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySplashBinding
    private var database = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        binding.logoImage.startAnimation(logoAnim)
        auth = FirebaseAuth.getInstance()

        checkAuthentication()

    }


    private fun checkAuthentication() {
        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear().apply()
        val user = auth.currentUser
        if(user!=null){
            val email = user.email.toString()
            val key = email.substringBefore("@")

            val databaseRef = database.getReference("Management")
            databaseRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var user = snapshot.getValue(Management::class.java)
                        if(user!=null){

                            editor.putString("name",user.name)
                            editor.putString("email",user.email)
                            editor.putString("phone",user.phone)
                            editor.putString("designation",user.designation)
                            editor.putString("userType",user.userType)
                            editor.putString("profilePic",user.profilePic)
                            editor.apply()

                            if(user.userType=="1"){
                                startActivity(Intent(this@SplashActivity, ProvostHomeActivity::class.java))
                                finish()
                                return

                            }else{

                                startActivity(Intent(this@SplashActivity, ManagerHomeActivity::class.java))
                                finish()
                                return

                            }
                        }
                    }
                    else {


                        //Student Login

                        val studentRef = database.getReference("Student")

                        studentRef.orderByChild("email").equalTo(email)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (childSnapshot in snapshot.children) {
                                            val student = childSnapshot.getValue(Student::class.java)

                                            if(student!=null){
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



                                                startActivity(Intent(this@SplashActivity, StudentHomeActivity::class.java))
                                                finish()
                                                return

                                            }



                                        }
                                    } else {
                                        //  Log.d("FirebaseData", "No student found with this email")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    //Log.e("FirebaseData", "Error: ${error.message}")
                                }
                            })



                    }
                }
                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(this@SplashActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }, 2500)

        }

    }






}
