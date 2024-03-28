package macc.AR.domain.usecase.rank

import androidx.lifecycle.MutableLiveData
import macc.AR.domain.manager.RankManager


class Fetch(
    private val rankManager: RankManager
){
    operator fun invoke(): MutableLiveData<List<String>> {
       return rankManager.fetch()
    }

}