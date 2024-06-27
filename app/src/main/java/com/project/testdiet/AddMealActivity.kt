package com.project.testdiet
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.testdiet.adapter.MealAdapter
import com.project.testdiet.databinding.ActivityAddMealBinding
import com.project.testdiet.model.Meal
import com.project.testdiet.model.MealDatabase
import com.project.testdiet.model.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "AddMealActivity"
class AddMealActivity : AppCompatActivity(), MealAdapter.OnItemClickListener {
    private lateinit var mealDatabase: MealDatabase
    private lateinit var mealAdapter: MealAdapter
    private lateinit var binding: ActivityAddMealBinding
    private val sharedViewModel: SharedViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private val addMealLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newMeals = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableArrayListExtra("selected_meals", Meal::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableArrayListExtra<Meal>("selected_meals")
            }
            newMeals?.let {
                Log.d(TAG, "New meals received: $it")
                saveMeals(it)
                it.forEach { meal ->
                    sharedViewModel.addMeal(meal)
                    mealAdapter.addMeal(meal)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mealDatabase = MealDatabase.getDatabase(this)

        mealAdapter = MealAdapter(mutableListOf(), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = mealAdapter

        sharedViewModel.meals.observe(this, Observer { meals ->
            Log.d(TAG, "Meals updated: $meals")
            mealAdapter.updateData(meals)
        })

        binding.addButton.setOnClickListener {
            Log.d(TAG, "Add button clicked")
            val intent = Intent(this, MealActivity::class.java)
            val mealType = sharedViewModel.mealType.value
            if (mealType != null) {
                intent.putExtra("mealType", mealType)
                Log.d(TAG, "Launching MealActivity with mealType: $mealType")
            } else {
                Log.e(TAG, "MealType is null, cannot launch MealActivity")
            }
            addMealLauncher.launch(intent)
        }

       val mealTypeFromIntent = intent.getStringExtra("mealType")
        if (mealTypeFromIntent != null) {
            sharedViewModel.setMealType(mealTypeFromIntent)
            loadMeals(mealTypeFromIntent)
        } else {
            Log.e(TAG, "Received null mealType from Intent")
        }
    }

    private fun saveMeals(meals: List<Meal>) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                meals.forEach {
                    Log.d(TAG, "Inserting meal: $it")
                    mealDatabase.mealDao().insert(it) }

                withContext(Dispatchers.Main) {
                    meals.forEach { meal ->
                        sharedViewModel.addMeal(meal)
                        mealAdapter.addMeal(meal)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting meal: ${e.message}", e)
            }
        }
    }

    private fun loadMeals(mealType: String) {
        mealDatabase.mealDao().getMealsByType(mealType).observe(this, Observer { mealsList ->
            Log.d(TAG, "Loaded meals: $mealsList")
            sharedViewModel.setMeals(mealsList)
        })
    }

    override fun onItemClick(meal: Meal) {
        Log.d(TAG, "Meal clicked: $meal")
    }
}
