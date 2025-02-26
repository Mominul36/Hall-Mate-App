package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.hallmate.Fragments.DayMealStatusFragment
import com.example.hallmate.Fragments.MHomeFragment
import com.example.hallmate.Fragments.MReportFragment
import com.example.hallmate.Fragments.MStaffHomeFragment
import com.example.hallmate.Fragments.MStudentHomeFragment
import com.example.hallmate.MainActivity
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityManagerHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class ManagerHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var navHome: LinearLayout
    lateinit var navStudent: LinearLayout
    lateinit var navStaff: LinearLayout
    lateinit var navMealManagement: LinearLayout

    lateinit var iconHome: ImageView
    lateinit var iconStudent: ImageView
    lateinit var iconStaff: ImageView
    lateinit var iconMealManagement: ImageView

    lateinit var txtHome: TextView
    lateinit var txtStudent: TextView
    lateinit var txtStaff: TextView
    lateinit var txtMealManagement: TextView


    lateinit var  name : String
    lateinit var  designation : String
    lateinit var  phone : String
    lateinit var  email : String
    lateinit var  password : String
    lateinit var  profilePic : String
    lateinit var  userType : String


    lateinit var binding: ActivityManagerHomeBinding
    var flag: Boolean = false
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var fragment = intent.getStringExtra("fragment")

        auth = FirebaseAuth.getInstance()
        initVariable()

         setNavigationItemColor(navHome)
        setManagerData()
           setFragment(MReportFragment())





        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)




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
            setFragment(MReportFragment())
        }

        navStudent.setOnClickListener {
             setNavigationItemColor(navStudent)
             setFragment(MStudentHomeFragment())
        }

        navStaff.setOnClickListener {
             setNavigationItemColor(navStaff)
            setFragment(MStaffHomeFragment())
        }

        navMealManagement.setOnClickListener {
             setNavigationItemColor(navMealManagement)
              setFragment(DayMealStatusFragment())
        }

    }


    private fun setManagerData() {
        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)

        name = sharedPreferences.getString("name","").toString()
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
        navStudent = findViewById(R.id.nav_student)
        navStaff = findViewById(R.id.nav_staff)
        navMealManagement = findViewById(R.id.nav_meal_management)

// ImageView items
        iconHome = findViewById(R.id.icon_home)
        iconStudent = findViewById(R.id.icon_student)
        iconStaff = findViewById(R.id.icon_staff)
        iconMealManagement = findViewById(R.id.icon_meal_management)


// TextView items
        txtHome = findViewById(R.id.txt_home)
        txtStudent = findViewById(R.id.txt_student)
        txtStaff = findViewById(R.id.txt_staff)
        txtMealManagement = findViewById(R.id.txt_meal_management)
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
                iconHome.setImageResource(R.drawable.home_base)
                iconHome.layoutParams = params
            }
            navStudent -> {
                txtStudent.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconStudent.setImageResource(R.drawable.student_base)
            }
            navStaff -> {
                txtStaff.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconStaff.setImageResource(R.drawable.staff_base)
            }
            navMealManagement -> {
                txtMealManagement.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconMealManagement.setImageResource(R.drawable.meal_status_base)
            }
        }
    }



    private fun setAllNavigationItemBlack() {
        // Set color for all TextViews
        txtHome.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtStudent.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtStaff.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtMealManagement.setTextColor(ContextCompat.getColor(this, R.color.black))

        // Set image resources for all ImageViews
        iconHome.setImageResource(R.drawable.home_black)
        iconStudent.setImageResource(R.drawable.student_black)
        iconStaff.setImageResource(R.drawable.staff_black)
        iconMealManagement.setImageResource(R.drawable.meal_status_black)

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
            R.id.nav_profile -> {
//                supportFragmentManager.beginTransaction().replace(R.id.content_frame, ProfileFragment()).commit()
                startActivity(Intent(this, MProfileActivity::class.java))
            }
            R.id.nav_notice -> {
//                supportFragmentManager.beginTransaction().replace(R.id.content_frame, ProfileFragment()).commit()
                startActivity(Intent(this, MNoticeActivity::class.java))
            }

            R.id.nav_day_meal_status -> {
//                supportFragmentManager.beginTransaction().replace(R.id.content_frame, ProfileFragment()).commit()
                startActivity(Intent(this, DayMealStatusActivity::class.java))
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
