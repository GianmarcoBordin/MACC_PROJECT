package com.mygdx.game.util

import com.mygdx.game.player.PlayerSkin

fun cleanUpString(input: String): String {

    val regex = Regex("[^A-Za-z0-9 ]")

    // Use the replace function with the regular expression to remove special characters
    return input.replace(regex, "")
}

fun isValidEmail(email: String): Boolean {
    // Define a regular expression pattern for email validation
    val emailRegex = Regex("^\\S+@\\S+\\.\\S+$")

    // Use the matches function to check if the email matches the pattern
    return emailRegex.matches(email)
}

fun String.removeCharactersAfterAt(): String {

    val atIndex = indexOf("@")


    return if (atIndex != -1) {
        substring(0, atIndex)
    } else {
        this
    }
}

fun fromIntegerToSkin(value: Int): PlayerSkin {
    return when (value) {
        1 -> PlayerSkin.GREEN
        2 -> PlayerSkin.RED
        3 -> PlayerSkin.YELLOW
        4 -> PlayerSkin.BLUE
        5 -> PlayerSkin.BLACK
        else -> {
            PlayerSkin.BLACK
        }
    }
}