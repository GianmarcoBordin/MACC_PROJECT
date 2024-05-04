package com.mygdx.game.domain.manager

import android.content.Context
import com.mygdx.game.data.manager.UpdateListener

interface AuthManager {
    suspend fun signIn(email:String,password:String)
    suspend fun bioSignIn(context: Context,callbacks: (String) -> Unit)
    suspend fun signUp(name:String,email:String,password:String,confirmPass:String)

    fun authCheck():Boolean

    fun setUpdateListener(ref: UpdateListener)
    suspend fun updatePlayerFirestore(name: String, newName: String): String
}