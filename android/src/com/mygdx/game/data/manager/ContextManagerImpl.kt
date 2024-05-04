package com.mygdx.game.data.manager

import android.content.Context
import com.mygdx.game.domain.manager.ContextManager


class ContextManagerImpl(private val context: Context) : ContextManager {
    override fun getContext(): Context {
        return context.applicationContext
    }
}