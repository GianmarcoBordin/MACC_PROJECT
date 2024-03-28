

package macc.AR.data.manager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import macc.AR.domain.api.DataRepository
import macc.AR.domain.manager.RankManager
import javax.inject.Inject

class RankManagerImpl @Inject constructor(private val dataRepository: DataRepository) :
    RankManager {
    private var updateListener: UpdateListener? = null
    private lateinit var contxt: Context


    override fun fetch(): MutableLiveData<List<String>> {
        return dataRepository.fetchData()
    }


    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref
    }


    interface FetchDataUseCase {
        fun execute(): LiveData<String>
    }
}


data class UserRank(val userId: String, val rank: Int)
