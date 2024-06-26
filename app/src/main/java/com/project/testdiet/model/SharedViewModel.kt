package com.project.testdiet.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _mealType = MutableLiveData<String>()
    val mealType: LiveData<String> get() = _mealType

    private val _meals = MutableLiveData<MutableList<Meal>>()
    val meals: LiveData<MutableList<Meal>> get() = _meals

    fun setMealType(mealType: String) {
        _mealType.value = mealType
    }
    fun setMeals(mealList: List<Meal>) {
        _meals.value = mealList.toMutableList()
    }

    fun addMeal(meal: Meal) {
        val currentList = _meals.value ?: mutableListOf()
        currentList.add(meal)
        _meals.value = currentList
    }
}
