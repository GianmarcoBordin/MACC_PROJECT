package com.mygdx.game.domain.api

import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Merge
import com.squareup.okhttp.ResponseBody
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RankApi {
    @GET("/user_rankings")
    fun fetchData(): Call<List<Rank>>

    @GET("/user_rankings")
    fun fetchUserData(@Query("user") user: String): Call<List<Rank>>

    @POST("/user_rankings")
    fun postRank(@Body request: Rank): Call<Result<ResponseBody>>

    @POST("/players")
    fun postPlayer(@Body request: Player): Call<Result<ResponseBody>>

    @GET("/game_items")
    fun getGameItem(@Query("user") user: String, @Query("rarity") rarity: String): Call<List<GameItem>>

    @GET("/game_items")
    fun getGameItemsUser(@Query("user") user: String): Call<List<GameItem>>

    @POST("/game_items")
    fun postGameItem(@Body request: GameItem): Call<Result<ResponseBody>>

    @POST("/game_items")
    fun mergeItems(@Body merge: Merge): Call<Result<ResponseBody>>
}

