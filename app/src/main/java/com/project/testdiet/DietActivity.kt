package com.project.testdiet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.testdiet.databinding.ActivityDietBinding
import com.project.testdiet.model.SharedViewModel

private const val TAG = "DietActivity"

class DietActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDietBinding
    private val sharedViewModel: SharedViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBreakfast.setOnClickListener {
            sharedViewModel.setMealType("breakfast")
            logViewModelValue()
            openMealActivity("breakfast")
        }
        binding.btnLunch.setOnClickListener {
            sharedViewModel.setMealType("lunch")
            logViewModelValue()
            openMealActivity("lunch")
        }
        binding.btnDinner.setOnClickListener {
            sharedViewModel.setMealType("dinner")
            logViewModelValue()
            openMealActivity("dinner")
        }
        binding.btnCompleteDiet.setOnClickListener {
            finish()
        }
    }

    private fun openMealActivity(mealType: String) {
        val intent = Intent(this, AddMealActivity::class.java)
        intent.putExtra("mealType", mealType)
        Log.d(TAG, "Launching AddMealActivity with mealType: $mealType")
        startActivity(intent)
    }

    private fun logViewModelValue() {
        sharedViewModel.mealType.observe(this) { mealType ->
            Log.d(TAG, "ViewModel mealType: $mealType")
        }
    }
}