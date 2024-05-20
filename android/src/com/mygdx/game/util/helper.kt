package com.mygdx.game.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mygdx.game.R
import com.mygdx.game.player.PlayerSkin
import com.mygdx.game.util.Constants.RARITY_1_DAMAGE
import com.mygdx.game.util.Constants.RARITY_1_HP
import com.mygdx.game.util.Constants.RARITY_2_DAMAGE
import com.mygdx.game.util.Constants.RARITY_2_HP
import com.mygdx.game.util.Constants.RARITY_3_DAMAGE
import com.mygdx.game.util.Constants.RARITY_3_HP
import com.mygdx.game.util.Constants.RARITY_4_DAMAGE
import com.mygdx.game.util.Constants.RARITY_4_HP
import com.mygdx.game.util.Constants.RARITY_5_DAMAGE
import com.mygdx.game.util.Constants.RARITY_5_HP

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

fun getItemDetails(itemRarity: Int): Triple<Int, Int, Int> {
    val defaultColor = R.drawable.gunner_green
    val defaultHp = RARITY_1_HP
    val defaultDamage = RARITY_1_DAMAGE

    return when (itemRarity) {
        1 -> Triple(R.drawable.gunner_green, RARITY_1_HP, RARITY_1_DAMAGE)
        2 -> Triple(R.drawable.gunner_red, RARITY_2_HP, RARITY_2_DAMAGE)
        3 -> Triple(R.drawable.gunner_yellow, RARITY_3_HP, RARITY_3_DAMAGE)
        4 -> Triple(R.drawable.gunner_blue, RARITY_4_HP, RARITY_4_DAMAGE)
        5 -> Triple(R.drawable.gunner_black, RARITY_5_HP, RARITY_5_DAMAGE)
        else -> Triple(defaultColor, defaultHp, defaultDamage)
    }
}

fun getItemDrawable(itemRarity: Int): Int {
    val defaultColor = R.drawable.gunner_green

    return when (itemRarity) {
        1 -> R.drawable.gunner_green
        2 -> R.drawable.gunner_red
        3 -> R.drawable.gunner_yellow
        4 -> R.drawable.gunner_blue
        5 -> R.drawable.gunner_black
        else -> defaultColor
    }
}

fun serializeObject(objectProperty: List<Pair<String, String>>): String{
    val gson = Gson()
    val jsonObject = JsonObject()

    objectProperty.forEach {
        jsonObject.addProperty(it.first,it.second)
    }

    return gson.toJson(jsonObject)
}

fun deserializeObject(jsonString: String): JsonObject {
    val gson = Gson()
    return gson.fromJson(jsonString, JsonObject::class.java)
}
