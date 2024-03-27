package macc.AR.data.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import macc.AR.domain.api.DataRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRepositoryImpl(private val rankApi: RankApi?) : DataRepository {
    override fun fetchData(): LiveData<String> {
        val data = MutableLiveData<String>()
        rankApi?.fetchData()?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                data.value = "Error: ${t.message}"
            }
        })
        return data
    }
}
