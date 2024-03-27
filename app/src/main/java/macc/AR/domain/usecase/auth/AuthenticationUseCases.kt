package macc.AR.domain.usecase.auth

import macc.AR.domain.usecase.Subscribe

data class AuthenticationUseCases (
    val signIn: SignIn,
    val signUp: SignUp,
    val bioSignIn:BioSignIn,
    val authCheck:AuthCheck,
    val subscribe: Subscribe

) {
}