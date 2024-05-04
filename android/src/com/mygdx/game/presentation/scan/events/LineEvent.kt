package com.mygdx.game.presentation.scan.events

import com.mygdx.game.data.dao.Line

sealed class LineEvent {
    data class AddNewLine(val line: Line) : LineEvent()
    data object DeleteAllLines : LineEvent()
}