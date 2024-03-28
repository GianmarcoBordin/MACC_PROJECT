package macc.AR.domain.manager

import androidx.lifecycle.MutableLiveData
import macc.AR.data.manager.UpdateListener


interface RankManager{
    fun fetch(): MutableLiveData<List<String>>

    fun setUpdateListener(ref: UpdateListener)
}