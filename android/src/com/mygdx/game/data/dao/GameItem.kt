package com.mygdx.game.data.dao

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
            val colorString = jsonObject.getAsJsonPrimitive("bitmap").asString
            val color : Int
            when(colorString){
                "green" -> color = Color.GREEN
                "red" -> color = Color.RED
                "yellow" -> color = Color.YELLOW
                "blue" -> color = Color.BLUE
                "black" -> color = Color.BLACK
                else->{
                    color = Color.GREEN
                }

            }
            val bitmapByteArray = getBitmapByteArray(color)
            val bitmap = convertByteArrayToBitmap(bitmapByteArray)
            return GameItem(id, rarity, hp, damage, bitmap)
        }

        private fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
            if (byteArray.isEmpty()){

            }
            val inputStream = ByteArrayInputStream(byteArray)
            return BitmapFactory.decodeStream(inputStream)
        }

        private fun getBitmapByteArray(color: Int): ByteArray {
            // Create a bitmap with a single pixel of the specified color
            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            bitmap.setPixel(0, 0, color)

            // Convert the bitmap to a byte array
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

    }
}