package macc.AR.domain.usecase.auth

data class AuthenticationUseCases (
    val signIn: SignIn,
    val signUp: SignUp,
    val signOut: SignOut,
    val confirm: Confirm,
    val sendEmail: SendEmail,
    val authCheck:AuthCheck,
    val subscribe: Subscribe

) {
}