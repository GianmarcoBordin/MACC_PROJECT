package com.mygdx.game.domain.api

import com.squareup.okhttp.ResponseBody
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RankApi {
    @GET("/user_rankings")
    fun fetchData(): Call<List<Rank>>

    @POST("/user_rankings")
    fun postRank(@Body request: Rank): Call<Result<ResponseBody>>

    @POST("/players")
    fun postPlayer(@Body request: Player): Call<Result<ResponseBody>>


}

