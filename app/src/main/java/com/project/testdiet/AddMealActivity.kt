package com.project.testdiet
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.testdiet.adapter.MealAdapter
import com.project.testdiet.databinding.ActivityAddMealBinding
import com.project.testdiet.model.Meal
import com.project.testdiet.model.MealDatabase
import com.project.testdiet.model.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddMealActivity : AppCompatActivity(), MealAdapter.OnItemClickListener {
    private lateinit var mealDatabase: MealDatabase
    private lateinit var mealAdapter: MealAdapter
    private lateinit var binding: ActivityAddMealBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    private val addMealLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newMeal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra("new_meal", Meal::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra("new_meal")
            }
            newMeal?.let {
                saveMeal(it)
                sharedViewModel.addMeal(it)
                mealAdapter.addMeal(it)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize meal database
        mealDatabase = MealDatabase.getDatabase(this)

        // Initialize meal adapter
        mealAdapter = MealAdapter(mutableListOf(), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = mealAdapter

        sharedViewModel.meals.observe(this, Observer { meals ->
            mealAdapter.updateData(meals)
        })

        binding.addButton.setOnClickListener {
            val intent = Intent(this, MealActivity::class.java)
            addMealLauncher.launch(intent)
        }

        // Load initial meals
        loadMeals(sharedViewModel.mealType.value ?: "")
    }

    private fun saveMeal(meal: Meal) {
        lifecycleScope.launch(Dispatchers.IO) {
            mealDatabase.mealDao().insert(meal)
        }
    }

    private fun loadMeals(mealType: String) {
        mealDatabase.mealDao().getMealsByType(mealType).observe(this, Observer { mealsList ->
            sharedViewModel.setMeals(mealsList)
        })
    }

    override fun onItemClick(meal: Meal) {
        // Implement item click handling
    }
}
