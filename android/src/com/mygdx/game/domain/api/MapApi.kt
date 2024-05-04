package com.mygdx.game.domain.api

import com.google.gson.JsonObject
import com.mygdx.game.data.dao.Map
import com.mygdx.game.util.Constants.MAPS_URL
import com.mygdx.game.util.Constants.MAPS_URL2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface MapApi{
    @GET(MAPS_URL)
    suspend fun calculateNavigationPath(
        @Query("start") startPoint: String,
        @Query("end") endPoint: String,
        @Query("api_key") apiKey: String
    ): Response<JsonObject>

    @POST(MAPS_URL2)
    suspend fun calculateNavigationPathPost(
        @Body requestBody: Map,
        @Header("Authorization") apiKey: String
    ): Response<JsonObject>
}
