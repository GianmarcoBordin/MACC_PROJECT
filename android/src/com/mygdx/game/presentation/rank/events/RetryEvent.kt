package com.mygdx.game.presentation.rank.events



sealed class RetryEvent(){
    class Retry() : RetryEvent()
}