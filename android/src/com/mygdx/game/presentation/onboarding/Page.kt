package macc.ar.presentation.onboarding

import androidx.annotation.DrawableRes
import com.mygdx.game.R


data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int
)

val pages = listOf(
    Page(
        title = "Welcome to our ar Token Collection App!",
        description = "Collect tokens in augmented reality and explore a new way of gaming.",
        image = R.drawable.onboarding1
    ),
    Page(
        title = "Discover Tokens with Maps!",
        description = "View collected tokens on maps to track your progress and find new challenges.",
        image = R.drawable.onboarding2
    ),
    Page(
        title = "Play multiplayer battle",
        description = "Play against other player with your collected skins.",
        image = R.drawable.onboarding3
    ),
    Page(
        title = "Compete and Climb the Rank!",
        description = "Compete with other users and climb the rank.",
        image = R.drawable.onboarding4
    )
)
