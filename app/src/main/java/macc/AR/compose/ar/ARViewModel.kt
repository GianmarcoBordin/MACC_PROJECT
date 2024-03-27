package macc.AR.compose.ar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import macc.AR.data.manager.UpdateListener
import macc.AR.domain.usecase.appEntry.AppEntryUseCases
import javax.inject.Inject

@HiltViewModel
class ARViewModel @Inject constructor(
    // TODO CHANGE
    // it was inserted to test
    private val appEntryUseCases: AppEntryUseCases,
) : ViewModel(), UpdateListener{

    override fun onUpdate(data:String) {
    }
}
