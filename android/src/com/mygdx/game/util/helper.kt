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

fun String.removeCharactersAfterAt(): String {
    // Find the index of "@" symbol in the string
    val atIndex = indexOf("@")

    // If "@" symbol exists, return the substring before it, else return the original string
    return if (atIndex != -1) {
        substring(0, atIndex)
    } else {
        this
    }
}