package com.weberbox.pifire.setup.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Url

interface SetupApi {

    @GET
    suspend fun getVersions(
        @Url url: String,
        @HeaderMap headerMap: Map<String, String>
    ): Response<ResponseBody>

}