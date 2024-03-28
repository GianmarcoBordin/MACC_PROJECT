package macc.AR.compose.rank


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import macc.AR.data.manager.UpdateListener
import macc.AR.domain.usecase.rank.RankUseCases
import macc.AR.util.Constants.USER_RANK
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class RankViewModel  @Inject constructor(
    private val rankUseCases: RankUseCases
): ViewModel(),UpdateListener{

    private val _rankData = MutableLiveData<List<String>>()
    val rankData: LiveData<List<String>> = _rankData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    init {
        // Set ViewModel as the listener for updates
        rankUseCases.subscribe.invoke(this,USER_RANK)
        // fetch data for user
        _rankData.value=rankUseCases.fetch().value
        _isLoading.value=false
        println("HERE view model"+_rankData.value.toString()+_data.value)
    }

    override fun onUpdate(data: String) {
        TODO("Not yet implemented")
    }

}

