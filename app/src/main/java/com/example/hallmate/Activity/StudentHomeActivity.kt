package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultRegistry
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Fragments.MHomeFragment
import com.example.hallmate.Fragments.SHomeFragment
import com.example.hallmate.MainActivity
import com.example.hallmate.Model.Meal
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityManagerHomeBinding
import com.example.hallmate.databinding.ActivityStudentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class StudentHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var hallId: String
    lateinit var studentId: String
    lateinit var name: String
    lateinit var email: String
    lateinit var phone: String
    lateinit var department: String
    lateinit var batch: String
    lateinit var roomNo: String
    lateinit var key: String
    lateinit var profilePic: String
    lateinit var password: String
    lateinit var mealCode: String

    var isCommitteeMember: Boolean = false
    var dueAmount: Double = 0.0
    var isStart: Boolean = false
    var isMutton: Boolean = false




    lateinit var navHome: LinearLayout
    lateinit var navMarket: LinearLayout
    lateinit var navService: LinearLayout
    lateinit var navMessage: LinearLayout
    lateinit var navProfile: LinearLayout

    lateinit var iconHome: ImageView
    lateinit var iconMarket: ImageView
    lateinit var iconCropCare: ImageView
    lateinit var iconAdvisor: ImageView
    lateinit var iconProfile: ImageView

    lateinit var txtHome: TextView
    lateinit var txtMarket: TextView
    lateinit var txtCropCare: TextView
    lateinit var txtAdvisor: TextView
    lateinit var txtProfile: TextView


    lateinit var load: Loading


    lateinit var binding: ActivityStudentHomeBinding
    var flag: Boolean = false
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)

        var fragment = intent.getStringExtra("fragment")

        auth = FirebaseAuth.getInstance()
        initVariable()
        getStudentData()
        setStudentData()
        setNavigationItemColor(navHome)
        setFragment(SHomeFragment())

        checkStartJourney()









        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        binding.btnStart.setOnClickListener{
            startJourney()
        }



        binding.rightNavIcon.setOnClickListener {
            if (binding.drawerlayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerlayout.closeDrawer(GravityCompat.END)
            } else {
                binding.drawerlayout.openDrawer(GravityCompat.END)
            }
        }

        binding.navView.setNavigationItemSelectedListener(this)




        navHome.setOnClickListener {
            setNavigationItemColor(navHome)
            setFragment(SHomeFragment())
        }

        navMarket.setOnClickListener {
            setNavigationItemColor(navMarket)
            //setFragment(MarketFragment())
        }

        navService.setOnClickListener {
            setNavigationItemColor(navService)
            //setFragment(ServiceFragment())
        }

        navMessage.setOnClickListener {
            setNavigationItemColor(navMessage)
            // setFragment(MessageFragment())
        }

        navProfile.setOnClickListener {
            setNavigationItemColor(navProfile)
            //setFragment(ProfileFragment())
        }



    }

    private fun checkStartJourney() {
       if(!isStart){
           binding.upper.visibility = View.GONE
           binding.navView.visibility = View.GONE
           binding.startLayout.visibility = View.VISIBLE
       }
    }

    private fun startJourney() {
        load.start()


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

                var breakFast = Meal(month,day,hallId,"BreakFast",false,false,false,false)
                var lunch = Meal(month,day,hallId,"Lunch",false,false,false,false)
                var dinner = Meal(month,day,hallId,"Dinner",false,false,false,false)

                val mealUpdates = mapOf(
                    "BreakFast" to breakFast,
                    "Lunch" to lunch,
                    "Dinner" to dinner
                )


                mealRef.child(month).child(day).child(hallId).updateChildren(mealUpdates)
                    .addOnSuccessListener {
                        if (i == 4 && d == 31) {
                            updateStart()
                        }
                    }
                    .addOnFailureListener {
                        // Handle failure
                        println("Failed to update meals: ${it.message}")
                    }


            }


        }





























    }

    private fun updateStart() {
       var studentRef = FirebaseDatabase.getInstance().getReference("Student")

        val student = mapOf<String, Any>(
            "isStart" to true
        )

        studentRef.child(hallId).updateChildren(student)
            .addOnSuccessListener {
              binding.upper.visibility = View.VISIBLE
                binding.navView.visibility = View.VISIBLE
                binding.startLayout.visibility = View.GONE
                load.end()

            }
            .addOnFailureListener {
                println("Failed to update student data: ${it.message}")
            }




    }

    private fun setStudentData() {
       binding.name.setText(name)
        binding.time.setText(getGreetingMessage())

    }

    fun getGreetingMessage(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)


        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }


    fun getStudentData() {
        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)

            hallId = sharedPreferences.getString("hallId", null).toString()
            studentId = sharedPreferences.getString("studentId", null).toString()
            name = sharedPreferences.getString("name", null).toString()
            email = sharedPreferences.getString("email", null).toString()
            phone = sharedPreferences.getString("phone", null).toString()
            department = sharedPreferences.getString("department", null).toString()
            batch = sharedPreferences.getString("batch", null).toString()
            roomNo = sharedPreferences.getString("roomNo", null).toString()
            isCommitteeMember = sharedPreferences.getBoolean("isCommitteeMember", false)
            dueAmount = sharedPreferences.getFloat("dueAmount", 0.0f).toDouble()
            key = sharedPreferences.getString("key", null).toString()
            profilePic = sharedPreferences.getString("profilePic", null).toString()
            password = sharedPreferences.getString("password", null).toString()
            mealCode = sharedPreferences.getString("mealCode", null).toString()
            isStart = sharedPreferences.getBoolean("isStart", false)
            isMutton = sharedPreferences.getBoolean("isMutton", false)

    }




    fun setFragment(fragment: Fragment){
        val fragmentManager : FragmentManager = supportFragmentManager
        val frammentTransition : FragmentTransaction = fragmentManager.beginTransaction()

        if(!flag){
            frammentTransition.add(R.id.frame,fragment)
            flag = true
        }
        else{
            frammentTransition.replace(R.id.frame,fragment)
        }
        frammentTransition.addToBackStack(null)
        frammentTransition.commit()
    }

    private fun initVariable() {
        // LinearLayout items
        navHome = findViewById(R.id.nav_home)
        navMarket = findViewById(R.id.nav_market)
        navService = findViewById(R.id.nav_service)
        navMessage = findViewById(R.id.nav_message)
        navProfile = findViewById(R.id.nav_profile)

// ImageView items
        iconHome = findViewById(R.id.icon_home)
        iconMarket = findViewById(R.id.icon_market)
        iconCropCare = findViewById(R.id.icon_crop_care)
        iconAdvisor = findViewById(R.id.icon_advisor)
        iconProfile = findViewById(R.id.icon_profile)

// TextView items
        txtHome = findViewById(R.id.txt_home)
        txtMarket = findViewById(R.id.txt_market)
        txtCropCare = findViewById(R.id.txt_crop_care)
        txtAdvisor = findViewById(R.id.txt_advisor)
        txtProfile = findViewById(R.id.txt_profile)
    }

    private fun setNavigationItemColor(layout: LinearLayout?) {
        // Reset all items to default color and icon
        setAllNavigationItemBlack()
        val newWidth = 50
        val newHeight = 50
        val params = LinearLayout.LayoutParams(newWidth, newHeight)

        // Set selected item color and icon based on the passed layout
        when (layout) {
            navHome -> {
                txtHome.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconHome.setImageResource(R.drawable.home)
                iconHome.layoutParams = params
            }
            navMarket -> {
                txtMarket.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconMarket.setImageResource(R.drawable.cart)
            }
            navService -> {
                txtCropCare.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconCropCare.setImageResource(R.drawable.crop_care)
            }
            navMessage -> {
                txtAdvisor.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconAdvisor.setImageResource(R.drawable.message)
            }
            navProfile -> {
                txtProfile.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconProfile.setImageResource(R.drawable.profile_user)
            }
        }
    }



    private fun setAllNavigationItemBlack() {
        // Set color for all TextViews
        txtHome.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtMarket.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtCropCare.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtAdvisor.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtProfile.setTextColor(ContextCompat.getColor(this, R.color.black))

        // Set image resources for all ImageViews
        iconHome.setImageResource(R.drawable.home)
        iconMarket.setImageResource(R.drawable.cart)
        iconCropCare.setImageResource(R.drawable.crop_care)
        iconAdvisor.setImageResource(R.drawable.message)
        iconProfile.setImageResource(R.drawable.profile_user)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerlayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerlayout.closeDrawer(GravityCompat.END)
        } else {
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.myproduct -> {
//                supportFragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment()).commit()
                //startActivity(Intent(this,MyProductActivity::class.java))
            }
            R.id.nav_profile -> {
//                supportFragmentManager.beginTransaction().replace(R.id.content_frame, ProfileFragment()).commit()
                startActivity(Intent(this, MProfileActivity::class.java))
            }


            R.id.nav_logout -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        binding.drawerlayout.closeDrawer(GravityCompat.END)
        return true
    }



}
