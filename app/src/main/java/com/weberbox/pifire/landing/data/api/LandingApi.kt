package com.weberbox.pifire.landing.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Url

interface LandingApi {

    @GET
    suspend fun getServerUuid(
        @Url url: String,
        @HeaderMap headerMap: Map<String, String>
    ): Response<ResponseBody>

}