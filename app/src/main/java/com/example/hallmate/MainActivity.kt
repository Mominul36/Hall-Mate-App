package com.example.hallmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.Activity.ManagementLoginActivity
import com.example.hallmate.Activity.ManagerHomeActivity
import com.example.hallmate.Activity.ProvostHomeActivity
import com.example.hallmate.Activity.StudentHomeActivity
import com.example.hallmate.Activity.StudentLoginActivity
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.DayMealStatus
import com.example.hallmate.Model.Hall
import com.example.hallmate.Model.Management
import com.example.hallmate.databinding.ActivityMainBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    lateinit var load : Loading2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading2(this)

        auth = FirebaseAuth.getInstance()
        binding.sLogin.setOnClickListener{
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }

        binding.mLogin.setOnClickListener{
            startActivity(Intent(this, ManagementLoginActivity::class.java))
        }

        checkAuthentication()




    }

    fun bal2(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword("mominul2@gmail.com", "adfsdf546")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("Firebase", "Registration successful!")

                } else {
                    val errorMessage = task.exception?.message
                    Log.e("Firebase", "Registration failed: $errorMessage")
                }
            }



    }

    private fun bal() {
        val databaseRef = database.getReference("DayMealStatus")


        for (d in 1..31) {

            var day:String = d.toString()
            if(d<10){
                day = "0"+day
            }

            var month = "05-2025"


            val dayStatusb = DayMealStatus(day.toString(),"BreakFast",true,false,false,
                0.0,0.0,0.0,0)
            val dayStatusl = DayMealStatus(day.toString(),"Lunch",true,false,false,
                0.0,0.0,0.0,0)
            val dayStatusd = DayMealStatus(day.toString(),"Dinner",true,false,false,
                0.0,0.0,0.0,0)


            databaseRef.child(month).child(day.toString()).child("BreakFast")
                .setValue(dayStatusb).addOnSuccessListener {
                  //  Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                }

            databaseRef.child(month).child(day.toString()).child("Lunch")
                .setValue(dayStatusl).addOnSuccessListener {
                    //Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                }

            databaseRef.child(month).child(day.toString()).child("Dinner")
                .setValue(dayStatusd).addOnSuccessListener {
                 //   Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                }


            databaseRef.child(month).child(day.toString()).child("isRamadan")
                .setValue(false).addOnSuccessListener {
                    //  Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                }

        }

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()










    }














    private fun createUser(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // User created successfully
                val user = authResult.user
                Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show()

                // Optionally, save user details to Firebase Realtime Database or Firestore


                // Redirect to the main activity or home screen
                startActivity(Intent(this, MainActivity::class.java))
                finish()  // Optional: Close the signup activity if needed
            }
            .addOnFailureListener { exception ->
                // Handle failure during user creation
                Toast.makeText(this, "Error creating user: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun signInUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // Sign in successful
                Toast.makeText(this, "Sign-in successful!", Toast.LENGTH_SHORT).show()

                // Redirect to the main activity or home screen
               // startActivity(Intent(this, MainActivity::class.java))
                finish()  // Optional: Close the login activity if needed
            }
            .addOnFailureListener { exception ->
                // Handle failure during sign-in
                Toast.makeText(this, "Sign-in failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun deleteUserAccount(password: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // Reauthenticate the user before deletion
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // Reauthentication successful, now delete the account
                    user.delete()
                        .addOnSuccessListener {
                            // Account deleted successfully
                            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                            // Optionally, you can redirect the user to the login screen or home page
                            FirebaseAuth.getInstance().signOut()
//                            startActivity(Intent(this, LoginActivity::class.java)) // or your desired activity
//                            finish() // close the current activity
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure when deleting the account
                            Toast.makeText(this, "Error deleting account: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle failure during reauthentication
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        // If the exception is due to wrong credentials, show "Wrong Password"
                        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle other errors during reauthentication
                        Toast.makeText(this, "Reauthentication failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Handle case where the user is not signed in
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show()
        }
    }



    private fun checkAuthentication() {
        binding.main.visibility = View.GONE
        load.start()
        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
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
                                startActivity(Intent(this@MainActivity, ProvostHomeActivity::class.java))
                                finish()
                                return

                            }else{

                                startActivity(Intent(this@MainActivity, ManagerHomeActivity::class.java))
                                finish()
                                return

                            }
                        }
                    }
                    else {


                        //Student Login

                        startActivity(Intent(this@MainActivity, StudentHomeActivity::class.java))
                        finish()
                        return



                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    load.end()
                    Toast.makeText(this@MainActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        load.end()
        binding.main.visibility = View.VISIBLE

    }
}