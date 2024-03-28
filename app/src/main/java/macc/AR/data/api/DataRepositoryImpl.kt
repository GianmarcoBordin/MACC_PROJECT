package macc.AR.data.api

import androidx.lifecycle.MutableLiveData
import macc.AR.data.Rank
import macc.AR.domain.api.DataRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRepositoryImpl(private val rankApi: RankApi?) : DataRepository {
    override fun fetchData(): MutableLiveData<List<String>> {
        println("HAHAHAHAHAH")
        val data = MutableLiveData<List<String>>()
        rankApi?.fetchData()?.enqueue(object : Callback<List<Rank>> {
            override fun onResponse(call: Call<List<Rank>>, response: Response<List<Rank>>) {
                if (response.isSuccessful) {
                    println("Score:"+response.body()?.get(0)?.score+"Username:"+response.body()?.get(0)?.username)
                } else {
                    println("HEREEEEEEEE2"+response.body().toString())
                    data.value = listOf("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Rank>>, t: Throwable) {
                println("HUHUHUHUH Error: ${t.message}")
                data.value = listOf("Error: ${t.message}")
            }
        })
        return data
    }
}
