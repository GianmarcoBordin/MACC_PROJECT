package com.mygdx.game.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun UserGreeting(name:String ,color: Color) {
    // Greetings for the user
    Text(
        text = "Hello, ${name}!",
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun LogoUserImage(name: String, modifier: Modifier = Modifier) {
    val initials = getInitials(name)

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(getRandomColor())
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = TextStyle(fontSize = 24.sp, color = Color.White),
            fontWeight = FontWeight.Bold
        )
    }
}
fun getInitials(name: String): String {
    return if (name.isNotEmpty()) {
        val words = name.split(" ")
        println(words)
        val firstInitial = words.firstOrNull()?.first()
        val lastInitial = words.lastOrNull()?.first()
        "$firstInitial$lastInitial"
    } else {
        ""
    }
}

fun getRandomColor(): Color {
    val colors = listOf(
        Color.Blue,
        Color.Red,
        Color.Green,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
        Color.Gray
    )
    return colors.random()
}