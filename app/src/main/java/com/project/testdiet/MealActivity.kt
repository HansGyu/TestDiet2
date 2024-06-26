package com.project.testdiet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.testdiet.adapter.FoodAdapter
import com.project.testdiet.databinding.ActivityMealBinding
import com.project.testdiet.model.FoodDTO
import com.project.testdiet.model.Meal
import com.project.testdiet.model.SharedViewModel
import com.project.testdiet.network.ApiService
import com.project.testdiet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MealActivity"

class MealActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var apiService: ApiService
    private lateinit var binding: ActivityMealBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        foodAdapter = FoodAdapter(emptyList()) { food ->
            addFood(food)
        }
        binding.recyclerView.adapter = foodAdapter

        apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)

        sharedViewModel.mealType.observe(this, Observer { mealType ->
            Log.d(TAG, "onCreate: mealType=$mealType")
            if (mealType != null) {
                loadFoods()
            }
        })


        binding.searchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    foodAdapter.filter.filter(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                foodAdapter.filter.filter(newText)
                return false
            }

        })
        loadFoods()

    }

    private fun loadFoods() {
        lifecycleScope.launch {
            val foods = withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getAllFoods().execute()
                    if (response.isSuccessful) {
                        response.body() ?: emptyList()
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            }
                foodAdapter.updateData(foods)
            }
        }

    private fun addFood(food: FoodDTO) {
        Toast.makeText(this, "${food.식품명} 추가됨", Toast.LENGTH_SHORT).show()

        val mealType = sharedViewModel.mealType.value
        if (mealType != null) {
            val newMeal = Meal(mealType = mealType, content = food.식품명)
            sharedViewModel.addMeal(newMeal)

            // Add this part to set the result and finish the activity
            val resultIntent = Intent().apply {
                putExtra("new_meal", newMeal)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
