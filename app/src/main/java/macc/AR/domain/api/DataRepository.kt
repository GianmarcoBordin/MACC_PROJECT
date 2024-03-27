package macc.AR.domain.api

import androidx.lifecycle.LiveData



interface DataRepository {
    fun fetchData(): LiveData<String>
}

