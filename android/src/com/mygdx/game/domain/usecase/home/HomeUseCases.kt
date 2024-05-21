package com.mygdx.game.domain.usecase.home

import com.mygdx.game.domain.usecase.Subscribe
import com.mygdx.game.domain.usecase.auth.AuthCheck
import com.mygdx.game.domain.usecase.auth.BioSignIn
import com.mygdx.game.domain.usecase.auth.SignIn
import com.mygdx.game.domain.usecase.auth.SignUp
import com.mygdx.game.domain.usecase.inventory.GetGameItemsUser

data class HomeUseCases (
    val getGameItemsUser: GetGameItemsUser
)