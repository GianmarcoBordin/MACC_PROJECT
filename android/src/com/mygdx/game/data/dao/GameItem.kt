package com.mygdx.game.data.dao

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

data class GameItem(
    val id: String,
    val rarity: Int,
    val hp: Int,
    val damage: Int,
    val bitmap: Bitmap
){
    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("rarity", rarity)
        jsonObject.addProperty("hp", hp)
        jsonObject.addProperty("damage", damage)
        val bitmapByteArray = convertBitmapToByteArray(bitmap)
        jsonObject.addProperty("bitmap", bitmapByteArray.toString())
        return gson.toJson(jsonObject)
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    companion object {
        fun fromJson(jsonString: String): GameItem {
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            val id = jsonObject.getAsJsonPrimitive("id").asString
            val rarity = jsonObject.getAsJsonPrimitive("rarity").asInt
            val hp = jsonObject.getAsJsonPrimitive("hp").asInt
            val damage = jsonObject.getAsJsonPrimitive("damage").asInt
            val bitmapByteArray = jsonObject.getAsJsonPrimitive("bitmap").asString.toByteArray()
            val bitmap = convertByteArrayToBitmap(bitmapByteArray)
            return GameItem(id, rarity, hp, damage, bitmap)
        }

        private fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
            val inputStream = ByteArrayInputStream(byteArray)
            return BitmapFactory.decodeStream(inputStream)
        }
    }
}