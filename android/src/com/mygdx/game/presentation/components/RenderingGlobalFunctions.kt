package com.mygdx.game.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.Text
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
fun UserGreeting(name:String) {
    // Greetings for the user
    Text(
        text = "Hello, ${name}!",
        color = MaterialTheme.colorScheme.onSecondaryContainer,
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
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = TextStyle(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Bold),
            //style = MaterialTheme.typography.displayMedium,
            //color = MaterialTheme.colorScheme.onSurface,
            //fontWeight = FontWeight.Bold
        )
    }
}
fun getInitials(name: String): String {
    return if (name.isNotEmpty()) {
        val words = name.split(" ")
        val firstInitial = words.firstOrNull()?.first()
        "$firstInitial"
    } else {
        ""
    }
}
