package com.mygdx.game.data.dao

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

data class GameItem(
    val id: String = "",
    val rarity: Int = 0,
    val hp: Int = 0,
    val damage: Int = 0,
    val bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Example default bitmap
){
    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("rarity", rarity)
        jsonObject.addProperty("hp", hp)
        jsonObject.addProperty("damage", damage)
        val bitmapBase64 = convertBitmapToBase64(bitmap)
        jsonObject.addProperty("bitmap", bitmapBase64)
        return gson.toJson(jsonObject)
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        fun fromJson(jsonString: String): GameItem {
            if (jsonString.isNotEmpty()){
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                val id = jsonObject.getAsJsonPrimitive("id").asString
                val rarity = jsonObject.getAsJsonPrimitive("rarity").asInt
                val hp = jsonObject.getAsJsonPrimitive("hp").asInt
                val damage = jsonObject.getAsJsonPrimitive("damage").asInt
                val bitmapString = jsonObject.getAsJsonPrimitive("bitmap").asString
                val bitmap = convertBitmapStringToBitmap(bitmapString)
                return GameItem(id, rarity, hp, damage, bitmap)
            }
            else
                return GameItem(id = "0", rarity = 0,hp = 1, damage = 1)


        }

        private fun convertBitmapStringToBitmap(bitmapString: String): Bitmap {
            // Decode the Base64 string into a ByteArray
            val decodedBytes: ByteArray = Base64.decode(bitmapString, Base64.DEFAULT)
            // Decode the ByteArray into a Bitmap
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
    }
}