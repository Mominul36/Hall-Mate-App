package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMainBinding
import com.example.hallmate.databinding.ActivityStudentLoginBinding

class StudentLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityStudentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToRegister.setOnClickListener{
            startActivity(Intent(this, StudentRegistrationActivity::class.java))
            finish()
        }






    }
}