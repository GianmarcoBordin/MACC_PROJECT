package macc.AR.domain.usecase.auth

data class AuthenticationUseCases (
    val signIn: SignIn,
    val signUp: SignUp,
    val bioSignIn:BioSignIn,
    val authCheck:AuthCheck,
    val subscribe: Subscribe

) {
}