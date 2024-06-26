package com.project.testdiet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.project.testdiet.databinding.ActivityDietBinding
import com.project.testdiet.model.SharedViewModel

private const val TAG = "DietActivity"

class DietActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDietBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBreakfast.setOnClickListener {
            sharedViewModel.setMealType("breakfast")
            logViewModelValue()
            openMealActivity()
        }
        binding.btnLunch.setOnClickListener {
            sharedViewModel.setMealType("lunch")
            logViewModelValue()
            openMealActivity()
        }
        binding.btnDinner.setOnClickListener {
            sharedViewModel.setMealType("dinner")
            logViewModelValue()
            openMealActivity()
        }
        binding.btnCompleteDiet.setOnClickListener {
            finish()
        }
    }

    private fun openMealActivity() {
        val intent = Intent(this, AddMealActivity::class.java)
        startActivity(intent)
    }

    private fun logViewModelValue() {
        sharedViewModel.mealType.observe(this) { mealType ->
            Log.d(TAG, "ViewModel mealType: $mealType")
        }
    }
}