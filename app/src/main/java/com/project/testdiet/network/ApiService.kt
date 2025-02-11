package com.project.testdiet.network

import com.project.testdiet.model.FoodDTO
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/foods")
    fun getAllFoods(): Call<List<FoodDTO>>

}