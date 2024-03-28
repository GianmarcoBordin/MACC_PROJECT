package macc.AR.data.api

import macc.AR.data.Rank
import retrofit2.Call
import retrofit2.http.GET

interface RankApi {
    @GET("/user_rankings")
    fun fetchData(): Call<List<Rank>>
}
