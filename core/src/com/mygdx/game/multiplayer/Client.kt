package com.mygdx.game.multiplayer

import com.mygdx.game.dto.Message


interface Client {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun send(message: Message)

}