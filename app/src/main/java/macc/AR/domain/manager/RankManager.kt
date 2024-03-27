package macc.AR.domain.manager

import androidx.lifecycle.LiveData
import macc.AR.data.manager.UpdateListener


interface RankManager{
    fun fetch(): LiveData<String>

    fun setUpdateListener(ref: UpdateListener)
}