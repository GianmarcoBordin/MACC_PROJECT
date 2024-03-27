package macc.AR.data.api

import retrofit2.Call
import retrofit2.http.GET

interface RankApi {
    @GET("/user_rankings")
    fun fetchData(): Call<String>
}
