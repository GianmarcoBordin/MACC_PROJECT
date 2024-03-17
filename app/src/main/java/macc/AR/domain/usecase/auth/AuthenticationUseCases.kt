package macc.AR.domain.usecase.auth

data class AuthenticationUseCases (
    val signIn: SignIn,
    val signUp: SignUp,
    val logOut: LogOut,
    val subscribe: Subscribe

) {
}