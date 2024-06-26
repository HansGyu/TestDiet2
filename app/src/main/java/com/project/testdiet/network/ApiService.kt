package com.project.testdiet.network

import com.project.testdiet.model.FoodDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/foods")
    fun getAllFoods(): Call<List<FoodDTO>>

    @POST("api/foods")
    fun addFood(@Body foodDTO: FoodDTO): Call<FoodDTO>
}