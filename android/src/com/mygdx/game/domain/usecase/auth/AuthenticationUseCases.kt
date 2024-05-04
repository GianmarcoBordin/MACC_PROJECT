package com.mygdx.game.domain.usecase.auth

import com.mygdx.game.domain.usecase.Subscribe

data class AuthenticationUseCases (
    val signIn: SignIn,
    val signUp: SignUp,
    val bioSignIn: BioSignIn,
    val authCheck: AuthCheck,
    val subscribe: Subscribe

)