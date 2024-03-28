package macc.AR.domain.api

import androidx.lifecycle.MutableLiveData


interface DataRepository {
    fun fetchData(): MutableLiveData<List<String>>
}

