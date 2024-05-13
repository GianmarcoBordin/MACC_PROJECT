package com.mygdx.game.data.api

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Ownership
import com.squareup.okhttp.ResponseBody
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.domain.api.RankApi
import com.mygdx.game.domain.api.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DataRepositoryImpl(private val rankApi: RankApi?) : DataRepository {

    override suspend fun fetchData(): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()
        try {
                val response = suspendCoroutine { continuation ->
                    rankApi?.fetchData()?.enqueue(object : Callback<List<Rank>> {
                        override fun onResponse(call: Call<List<Rank>>, response: Response<List<Rank>>) {
                            continuation.resume(response)
                        }

                        override fun onFailure(call: Call<List<Rank>>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }
                    })
                }

                if (response.isSuccessful) {
                    val ranks = response.body()
                    if (ranks == emptyList<Rank>()){
                        data.value = emptyList()
                    }
                    else if (ranks != null) {
                        val rankInfo = ranks.map {
                            "${it.username} ${it.score}" }
                        data.value = rankInfo
                    } else {
                        // Empty response body
                        data.value = listOf("Error: ")
                    }
                } else {
                    // Unsuccessful response
                    Log.d(TAG,"Error: ${response.code()}"+response)
                    data.value = listOf("Error: ")
                }
        } catch (e: Exception) {
                // Failure
                Log.d(TAG,"Error: ${e.message}"+e.printStackTrace())
                data.value = listOf("Error: ")
            }


        return data
    }

    override suspend fun fetchUserData(user : String): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()
        try {
            CoroutineScope(Dispatchers.IO).launch {

                val response = suspendCoroutine { continuation ->
                    rankApi?.fetchUserData(user)?.enqueue(object : Callback<List<Rank>> {
                        override fun onResponse(
                            call: Call<List<Rank>>,
                            response: Response<List<Rank>>
                        ) {
                            continuation.resume(response)
                        }

                        override fun onFailure(call: Call<List<Rank>>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }
                    })
                }

                if (response.isSuccessful) {
                    val ranks = response.body()
                    if (ranks == emptyList<Rank>()) {
                        data.postValue(emptyList())
                    } else if (ranks != null) {
                        val rankInfo = ranks.map {
                            "${it.username} ${it.score}"
                        }
                        data.postValue(rankInfo)
                    } else {
                        // Empty response body
                        data.postValue(emptyList())
                    }
                } else {
                    // Unsuccessful response
                    Log.d(TAG, "Error: ${response.code()}" + response)
                    data.postValue(listOf("Error: "))

                }
            }
        } catch (e: Exception) {
            // Failure
            Log.d(TAG,"Error: ${e.message}"+e.printStackTrace())
            data.postValue(listOf("Error: "))
        }


        return data
    }


    override suspend fun postRank(request: Rank): LiveData<String> {
            val resultLiveData = MutableLiveData<String>()

            try {
                val response = suspendCoroutine { continuation ->
                    rankApi?.postRank(request)?.enqueue(object : Callback<Result<ResponseBody>> {
                        override fun onResponse(
                            call: Call<Result<ResponseBody>>,
                            response: Response<Result<ResponseBody>>
                        ) {
                            continuation.resume(response)                        }

                        override fun onFailure(call: Call<Result<ResponseBody>>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }
                    })
                }
                resultLiveData.postValue(response.isSuccessful.toString())
            } catch (e: Exception) {
                resultLiveData.postValue(e.message)
            }

            return resultLiveData
        }

    override suspend fun postPlayer(request: Player): LiveData<String> {
        val resultLiveData = MutableLiveData<String>()

        try {
            val response = suspendCoroutine { continuation ->
                rankApi?.postPlayer(request)?.enqueue(object : Callback<Result<ResponseBody>> {
                    override fun onResponse(
                        call: Call<Result<ResponseBody>>,
                        response: Response<Result<ResponseBody>>
                    ) {
                        continuation.resume(response)                        }

                    override fun onFailure(call: Call<Result<ResponseBody>>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }
            resultLiveData.value =response.isSuccessful.toString()
        } catch (e: Exception) {
            resultLiveData.value = e.message
        }

        return resultLiveData
    }




    override suspend fun postGameItem(request: GameItem): LiveData<String> {
        val resultLiveData = MutableLiveData<String>()

        try {
            val response = suspendCoroutine { continuation ->
                rankApi?.postGameItem(request)?.enqueue(object : Callback<Result<ResponseBody>> {
                    override fun onResponse(
                        call: Call<Result<ResponseBody>>,
                        response: Response<Result<ResponseBody>>
                    ) {
                        continuation.resume(response)                        }

                    override fun onFailure(call: Call<Result<ResponseBody>>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }
            resultLiveData.value =response.isSuccessful.toString()
        } catch (e: Exception) {
            resultLiveData.value = e.message
        }

        return resultLiveData
    }
    override suspend fun getGameItem(user: String, rarity:String): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()
        try {
                val response = suspendCoroutine { continuation ->
                    rankApi?.getGameItem(user, rarity)?.enqueue(object : Callback<List<GameItem>> {
                        override fun onResponse(
                            call: Call<List<GameItem>>,
                            response: Response<List<GameItem>>
                        ) {
                            continuation.resume(response)
                        }

                        override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }
                    })
                }

                if (response.isSuccessful) {
                    val ranks = response.body()
                    if (ranks == emptyList<GameItem>()) {
                        data.postValue(emptyList())
                    }
                    if (ranks != null) {
                        println(response.body())
                        val rankInfo = ranks.map {
                            "${it.id} ${it.rarity} ${it.hp} ${it.damage}"
                        }

                        data.postValue(rankInfo)
                    } else {
                        // Empty response body
                        data.postValue(emptyList())
                    }
                } else {
                    // Unsuccessful response
                    Log.d(TAG, "Error: ${response.code()}" + response)
                    data.postValue(listOf("Error: "))
                }
        } catch (e: Exception) {
            // Failure
            Log.d(TAG,"Error: ${e.message}"+e.printStackTrace())
            data.postValue(listOf("Error: "))
        }

        return data
    }

    override suspend fun getGameItemsUser(user: String): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()
        try {
            val response = suspendCoroutine { continuation ->
                rankApi?.getGameItemsUser(user)?.enqueue(object : Callback<List<GameItem>> {
                    override fun onResponse(
                        call: Call<List<GameItem>>,
                        response: Response<List<GameItem>>
                    ) {
                        continuation.resume(response)
                    }

                    override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }

            if (response.isSuccessful) {
                val ranks = response.body()
                if (ranks == emptyList<GameItem>()) {
                    data.postValue(emptyList())
                }
                if (ranks != null) {
                    val rankInfo = ranks.map {
                        "${it.id} ${it.rarity} ${it.hp} ${it.damage}"
                    }

                    data.postValue(rankInfo)
                } else {
                    // Empty response body
                    data.postValue(emptyList())
                }
            } else {
                // Unsuccessful response
                Log.d(TAG, "Error: ${response.code()}" + response)
                data.postValue(listOf("Error: "))
            }
        } catch (e: Exception) {
            // Failure
            Log.d(TAG,"Error: ${e.message}"+e.printStackTrace())
            data.postValue(listOf("Error: "))
        }

        return data
    }


    override suspend fun postOwnership(request: Ownership): LiveData<String> {
        val resultLiveData = MutableLiveData<String>()

        try {
            val response = suspendCoroutine { continuation ->
                rankApi?.postOwnership(request)?.enqueue(object : Callback<Result<ResponseBody>> {
                    override fun onResponse(
                        call: Call<Result<ResponseBody>>,
                        response: Response<Result<ResponseBody>>
                    ) {
                        continuation.resume(response)                        }

                    override fun onFailure(call: Call<Result<ResponseBody>>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }
            resultLiveData.value =response.isSuccessful.toString()
        } catch (e: Exception) {
            resultLiveData.value = e.message
        }

        return resultLiveData
    }

    override suspend fun getOwnership(user: String, item: String): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()
        try {
            val response = suspendCoroutine { continuation ->
                rankApi?.getOwnership(user, item)?.enqueue(object : Callback<List<Ownership>> {
                    override fun onResponse(call: Call<List<Ownership>>, response: Response<List<Ownership>>) {
                        continuation.resume(response)
                    }

                    override fun onFailure(call: Call<List<Ownership>>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }

            if (response.isSuccessful) {
                val ranks = response.body()
                if (ranks == emptyList<Ownership>()){
                    data.value = emptyList()
                }
                if (ranks != null) {
                    val rankInfo = ranks.map {
                        "${it.username} ${it.itemId}" }
                    data.value = rankInfo
                } else {
                    // Empty response body
                    data.value = emptyList()
                }
            } else {
                // Unsuccessful response
                Log.d(TAG,"Error: ${response.code()}"+response)
                data.value = listOf("Error: ")
            }
        } catch (e: Exception) {
            // Failure
            Log.d(TAG,"Error: ${e.message}"+e.printStackTrace())
            data.value = listOf("Error: ")
        }


        return data
    }
}
