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
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.example.hallmate.Model.HallIdEmail
import com.example.hallmate.Model.Management
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.MealForRV
import com.example.hallmate.Model.Menu
import com.example.hallmate.Model.Student
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

    var bb = "Hello"

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

      //  setMenuData()


    //temp()



    }


    fun setMenuData() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Menu")

        val menuData = mapOf(
            "Saturday" to mapOf(
                "Sahari" to listOf(
                    Menu("Porridge", "Unlimited"),
                    Menu("Paratha", "3 pieces"),
                    Menu("Fried Egg", "2 eggs")
                ),
                "BreakFast" to listOf(
                    Menu("Rice", "Unlimited"),
                    Menu("Toast", "2 slices"),
                    Menu("Pancakes", "3 pieces")
                ),
                "Lunch" to listOf(
                    Menu("Biryani", "2 plates"),
                    Menu("Grilled Chicken", "1 plate"),
                    Menu("Pasta", "1 serving")
                ),
                "Iftar" to listOf(
                    Menu("Fruits", "Light"),
                    Menu("Samosas", "4 pieces"),
                    Menu("Spring Rolls", "3 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Chicken Curry", "1 plate"),
                    Menu("Mutton Curry", "1 bowl"),
                    Menu("Fish Curry", "1 bowl")
                )
            ),
            "Sunday" to mapOf(
                "Sahari" to listOf(
                    Menu("Bagels", "2 pieces"),
                    Menu("Yogurt", "1 cup"),
                    Menu("Cereal", "1 bowl")
                ),
                "BreakFast" to listOf(
                    Menu("Boiled Eggs", "3 eggs"),
                    Menu("Sandwich", "2 slices"),
                    Menu("French Toast", "2 slices")
                ),
                "Lunch" to listOf(
                    Menu("Fried Rice", "1 plate"),
                    Menu("Beef Stew", "1 bowl"),
                    Menu("Pizza", "2 slices")
                ),
                "Iftar" to listOf(
                    Menu("Baklava", "2 pieces"),
                    Menu("Stuffed Dates", "4 pieces"),
                    Menu("Dates", "5 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Kebabs", "5 pieces"),
                    Menu("Grilled Fish", "1 plate"),
                    Menu("Lamb Curry", "1 bowl")
                )
            ),
            "Monday" to mapOf(
                "Sahari" to listOf(
                    Menu("Oats", "1 bowl"),
                    Menu("Fried Egg", "2 eggs"),
                    Menu("Paratha", "3 pieces")
                ),
                "BreakFast" to listOf(
                    Menu("Pancakes", "3 pieces"),
                    Menu("Toast", "2 slices"),
                    Menu("Boiled Eggs", "3 eggs")
                ),
                "Lunch" to listOf(
                    Menu("Beef Stew", "1 bowl"),
                    Menu("Chicken Biryani", "2 plates"),
                    Menu("Pasta", "1 serving")
                ),
                "Iftar" to listOf(
                    Menu("Dates", "5 pieces"),
                    Menu("Spring Rolls", "3 pieces"),
                    Menu("Samosas", "4 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Fish Curry", "1 bowl"),
                    Menu("Grilled Chicken", "1 plate"),
                    Menu("Lamb Curry", "1 bowl")
                )
            ),
            "Tuesday" to mapOf(
                "Sahari" to listOf(
                    Menu("Bagels", "2 pieces"),
                    Menu("Yogurt", "1 cup"),
                    Menu("Cereal", "1 bowl")
                ),
                "BreakFast" to listOf(
                    Menu("Sandwich", "2 slices"),
                    Menu("French Toast", "2 slices"),
                    Menu("Boiled Eggs", "3 eggs")
                ),
                "Lunch" to listOf(
                    Menu("Fried Rice", "1 plate"),
                    Menu("Grilled Chicken", "1 plate"),
                    Menu("Pasta", "1 serving")
                ),
                "Iftar" to listOf(
                    Menu("Baklava", "2 pieces"),
                    Menu("Stuffed Dates", "4 pieces"),
                    Menu("Dates", "5 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Kebabs", "5 pieces"),
                    Menu("Grilled Fish", "1 plate"),
                    Menu("Lamb Curry", "1 bowl")
                )
            ),
            "Wednesday" to mapOf(
                "Sahari" to listOf(
                    Menu("Oats", "1 bowl"),
                    Menu("Fried Egg", "2 eggs"),
                    Menu("Paratha", "3 pieces")
                ),
                "BreakFast" to listOf(
                    Menu("Pancakes", "3 pieces"),
                    Menu("Toast", "2 slices"),
                    Menu("Boiled Eggs", "3 eggs")
                ),
                "Lunch" to listOf(
                    Menu("Beef Stew", "1 bowl"),
                    Menu("Chicken Biryani", "2 plates"),
                    Menu("Pasta", "1 serving")
                ),
                "Iftar" to listOf(
                    Menu("Dates", "5 pieces"),
                    Menu("Spring Rolls", "3 pieces"),
                    Menu("Samosas", "4 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Fish Curry", "1 bowl"),
                    Menu("Grilled Chicken", "1 plate"),
                    Menu("Lamb Curry", "1 bowl")
                )
            ),
            "Thursday" to mapOf(
                "Sahari" to listOf(
                    Menu("Cereal", "1 bowl"),
                    Menu("Fried Egg", "2 eggs"),
                    Menu("Paratha", "3 pieces")
                ),
                "BreakFast" to listOf(
                    Menu("Toast", "2 slices"),
                    Menu("Boiled Eggs", "3 eggs"),
                    Menu("Pancakes", "3 pieces")
                ),
                "Lunch" to listOf(
                    Menu("Grilled Chicken", "1 plate"),
                    Menu("Pizza", "2 slices"),
                    Menu("Pasta", "1 serving")
                ),
                "Iftar" to listOf(
                    Menu("Samosas", "4 pieces"),
                    Menu("Spring Rolls", "3 pieces"),
                    Menu("Baklava", "2 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Mutton Curry", "1 bowl"),
                    Menu("Chicken Curry", "1 plate"),
                    Menu("Fish Curry", "1 bowl")
                )
            ),
            "Friday" to mapOf(
                "Sahari" to listOf(
                    Menu("Paratha", "3 pieces"),
                    Menu("Oats", "1 bowl"),
                    Menu("Fried Egg", "2 eggs")
                ),
                "BreakFast" to listOf(
                    Menu("Waffles", "3 pieces"),
                    Menu("Boiled Eggs", "3 eggs"),
                    Menu("Sandwich", "2 slices")
                ),
                "Lunch" to listOf(
                    Menu("Pizza", "2 slices"),
                    Menu("Fried Rice", "1 plate"),
                    Menu("Pasta", "1 serving")
                ),
                "Iftar" to listOf(
                    Menu("Stuffed Dates", "4 pieces"),
                    Menu("Baklava", "2 pieces"),
                    Menu("Spring Rolls", "3 pieces")
                ),
                "Dinner" to listOf(
                    Menu("Steak", "1 piece"),
                    Menu("Grilled Chicken", "1 plate"),
                    Menu("Fish Curry", "1 bowl")
                )
            )
        )

        // Set data for all days and periods
        menuData.forEach { (day, periods) ->
            periods.forEach { (period, menus) ->
                menus.forEachIndexed { index, menu ->
                    ref.child(day).child(period).child("Menu${index + 1}").setValue(menu)
                        .addOnSuccessListener {
                            println("Menu for $day - $period - Menu${index + 1} updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            println("Failed to update menu for $day - $period: ${e.message}")
                        }
                }
            }
        }
    }


   fun temp(){


       val ref = database.getReference("Hall")






           val updates = mapOf<String, Any>(
               "lastHallId" to 1025,
               "sahariStartTime" to "04:25",
               "sahariFinishTime" to "05:25",
               "breakfastStartTime" to "07:00",
               "breakfastFinishTime" to "09:00",
               "lunchStartTime" to "14:00",
               "lunchFinishTime" to "15:00",
               "iftarStartTime" to "18:00",
               "iftarFinishTime" to "19:00",
               "dinnerStartTime" to "21:00",
               "dinnerFinishTime" to "22:00"
           )


       ref.updateChildren(updates).addOnSuccessListener {  }












    }




    private fun checkAuthentication() {
        binding.main.visibility = View.GONE
        load.start()
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
                                                editor.putBoolean("isStart", student.isLock ?: false)
                                                editor.putBoolean("isMutton", student.isMutton ?: false)

                                                editor.apply()



                                                startActivity(Intent(this@MainActivity, StudentHomeActivity::class.java))
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
                    load.end()
                    Toast.makeText(this@MainActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        load.end()
        binding.main.visibility = View.VISIBLE

    }




    private fun bal() {
        val databaseRef = database.getReference("DayMealStatus")


        for (d in 1..28) {

            var day:String = d.toString()
            if(d<10){
                day = "0"+day
            }

            var month = "02-2025"


            val dayMealStatus = DayMealStatus(day,month,false,
                true,false,false,0.0,0.0,0.0,0,0,
                true,false,false,0.0,0.0,0.0,0,0,
                true,false,false,0.0,0.0,0.0,0,0)




            databaseRef.child(month).child(day.toString())
                .setValue(dayMealStatus).addOnSuccessListener {
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




}