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
import com.example.hallmate.Class.Loading
import com.example.hallmate.Fragments.SHallCardFragment
import com.example.hallmate.Fragments.SHomeFragment
import com.example.hallmate.Fragments.SMealStatusFragment
import com.example.hallmate.MainActivity
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityStudentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
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
    var isLock: Boolean = false
    var isMutton: Boolean = false




    lateinit var navHome: LinearLayout
    lateinit var navCard: LinearLayout
    lateinit var navMealStatus: LinearLayout

    lateinit var iconHome: ImageView
    lateinit var iconMCard: ImageView
    lateinit var iconMealStatus: ImageView

    lateinit var txtHome: TextView
    lateinit var txtCard: TextView
    lateinit var txtMealStatus: TextView




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
            setFragment(SHomeFragment())
        }

        navCard.setOnClickListener {
            setNavigationItemColor(navCard)
            setFragment(SHallCardFragment())
        }

        navMealStatus.setOnClickListener {
            setNavigationItemColor(navMealStatus)
            setFragment(SMealStatusFragment())
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
            isLock = sharedPreferences.getBoolean("isLock", false)
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
        navCard  = findViewById(R.id.nav_card)
        navMealStatus = findViewById(R.id.nav_meal_status)


// ImageView items
        iconHome = findViewById(R.id.icon_home)
        iconMCard = findViewById(R.id.icon_card)
        iconMealStatus = findViewById(R.id.icon_meal_status)

// TextView items
        txtHome = findViewById(R.id.txt_home)
        txtCard = findViewById(R.id.txt_card)
        txtMealStatus = findViewById(R.id.txt_meal_status)


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
            navCard -> {
                txtCard.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconMCard.setImageResource(R.drawable.card_bage)
            }
            navMealStatus -> {
                txtMealStatus.setTextColor(ContextCompat.getColor(this, R.color.base_color))
                iconMealStatus.setImageResource(R.drawable.meal_status_base)
            }
        }
    }



    private fun setAllNavigationItemBlack() {
        // Set color for all TextViews
        txtHome.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtCard.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtMealStatus.setTextColor(ContextCompat.getColor(this, R.color.black))

        // Set image resources for all ImageViews
        iconHome.setImageResource(R.drawable.home_black)
        iconMCard.setImageResource(R.drawable.card_black)
        iconMealStatus.setImageResource(R.drawable.meal_status_black)


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
                startActivity(Intent(this, SStudentProfileActivity::class.java))
            }

            R.id.nav_bill -> {
//                supportFragmentManager.beginTransaction().replace(R.id.content_frame, ProfileFragment()).commit()
                startActivity(Intent(this, SStudentHallBillActivity::class.java))
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
