package macc.AR.domain.usecase.auth

data class AuthenticationUseCases (
    val signIn: SignIn,
    val signUp: SignUp,
    val bioSignIn:BioSignIn,
    val confirm: Confirm,
    val sendEmail: SendEmail,
    val authCheck:AuthCheck,
    val subscribe: Subscribe

) {
}