package macc.AR.domain.usecase.rank

import androidx.lifecycle.LiveData
import macc.AR.domain.manager.RankManager


class Fetch(
    private val rankManager: RankManager
){
    operator fun invoke(): LiveData<String> {
       return rankManager.fetch()
    }

}