package com.android.example.liverpool.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access point liverpool
 */
interface LiverpoolService {

    @GET("appclienteservices/services/v3/plp")
    fun searchPlp(@Query("force-plp") force: String, @Query("search-string") search: String, @Query("page-number") page: Int, @Query("number-of-items-per-page") items: Int): LiveData<ApiResponse<PlpSearchProductResponse>>

    @GET("appclienteservices/services/v3/plp")
    fun searchPlpCall(@Query("force-plp") force: String, @Query("search-string") search: String, @Query("page-number") page: Int, @Query("number-of-items-per-page") items: Int): Call<PlpSearchProductResponse>
}
