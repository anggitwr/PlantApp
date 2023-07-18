package com.anggitwr.myplantapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anggitwr.myplantapp.databinding.ActivityOnboarding2Binding

class OnboardingActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityOnboarding2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboarding2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        action()
    }

    private fun action(){

        binding.btnOnb1Lanjut.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }
    }
}