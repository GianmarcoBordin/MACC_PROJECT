package com.mygdx.game.presentation.rank


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mygdx.game.domain.usecase.rank.RankUseCases
import com.mygdx.game.presentation.rank.events.RankUpdateEvent
import com.mygdx.game.presentation.rank.events.RetryEvent
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class RankViewModel  @Inject constructor(
    private val rankUseCases: RankUseCases
): ViewModel() {

    private val _rankData = MutableLiveData<List<String>?>()
    val rankData: MutableLiveData<List<String>?> = _rankData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    init {
        // fetch data for user
        fetchData()
    }


    override fun onCleared() {
        super.onCleared()
        release()
    }
    private fun fetchData() {
        viewModelScope.launch {
            // Set loading state
            _isLoading.value = true
            _isError.value=false
            delay(1000)
            try {
                // Fetch data asynchronously
                val result = rankUseCases.fetch()

                if (result.value== null || result.value?.isEmpty() == true || result.value == listOf("Error: ")){
                    _isError.value=true
                }else{
                    // Update rank data
                    _rankData.value = result.value
                }
            } catch (e: Exception) {
                // Handle error
                _isError.value=true
            } finally {
                // Set loading state to false regardless of success or failure
                _isLoading.value = false
            }
        }
    }

    fun onRankUpdateEvent(event: RankUpdateEvent) {
        when (event) {
            is RankUpdateEvent.RankUpdate -> {
                goRankUpdate()
            }

        }
    }
    fun onRetryEvent(event: RetryEvent) {
        when (event) {
            is RetryEvent.Retry -> {
                goRetry()
            }

        }
    }

    private fun goRankUpdate(){
        fetchData()
    }
    private fun goRetry(){
        fetchData()
    }

    fun release() {
        _rankData.value = null

    }

    fun resume() {
       fetchData()
    }

}

