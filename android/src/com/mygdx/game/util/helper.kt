package com.mygdx.game.util

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