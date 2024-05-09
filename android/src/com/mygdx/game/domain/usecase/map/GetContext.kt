package com.mygdx.game.domain.usecase.map

import android.content.Context
import com.mygdx.game.domain.manager.ContextManager


class GetContext(
    private val contextManager: ContextManager
) {
     operator fun invoke(): Context {
        return contextManager.getContext()
    }

}