package com.project.testdiet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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

        sharedViewModel.totalEnergy.observe(this, Observer { totalEnergy ->
            updateTotalNutritionalValues()
        })

        sharedViewModel.totalProtein.observe(this, Observer { totalProtein ->
            updateTotalNutritionalValues()
        })

        sharedViewModel.totalFat.observe(this, Observer { totalFat ->
            updateTotalNutritionalValues()
        })

        sharedViewModel.totalCarbs.observe(this, Observer { totalCarbs ->
            updateTotalNutritionalValues()
        })

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
        // 초기 데이터 로드
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

    private fun updateTotalNutritionalValues() {
        val totalEnergy = sharedViewModel.totalEnergy.value ?: 0f
        val totalProtein = sharedViewModel.totalProtein.value ?: 0f
        val totalFat = sharedViewModel.totalFat.value ?: 0f
        val totalCarbs = sharedViewModel.totalCarbs.value ?: 0f

        val totalNutritionalValues = """
            Total Energy: $totalEnergy kcal
            Total Protein: $totalProtein g
            Total Fat: $totalFat g
            Total Carbs: $totalCarbs g
        """.trimIndent()

        binding.tvTotalNutritionalValues.text = totalNutritionalValues
    }
}