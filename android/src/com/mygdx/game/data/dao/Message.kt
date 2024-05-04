package com.mygdx.game.data.dao

import java.io.Serializable

data class Message(
    val error:String?,
    val sender: String?,
    val receiver: String?,
    val message: String?
) :Serializable
