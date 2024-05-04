package com.mygdx.game.presentation.scan.events

import android.graphics.Bitmap

sealed class BitmapEvent {
    data class BitmapCreated (val bitmap: Bitmap) : BitmapEvent()
}