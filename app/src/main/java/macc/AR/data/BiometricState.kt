package macc.AR.data

import android.content.Context
import macc.AR.data.manager.retrieveFirebaseUserIdAndBiometricCredentials
import macc.AR.data.manager.saveFirebaseUserIdAndBiometricCredentials
import javax.inject.Singleton


// Data class to hold biometric credentials
@Singleton
data class BiometricState(val _email: String, val _password: String)
{
    private var email=_email
    private var password= _password

    fun setBio(context: Context){
        saveFirebaseUserIdAndBiometricCredentials(context,this)
    }
    fun setCredentials( mail: String, pass:String){
        email=mail
        password=pass
    }
    fun getBio():Pair <String,String>{
        return Pair(email,password)
    }

    fun initBio(context: Context): Pair<String, String>? {
        return retrieveFirebaseUserIdAndBiometricCredentials(context)
    }
}
