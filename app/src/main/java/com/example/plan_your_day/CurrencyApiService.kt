package com.example.plan_your_day

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("latest/{base}")
    fun getLatestRates(
        @Path("base") base: String
    ): Call<CurrencyResponse>
}


