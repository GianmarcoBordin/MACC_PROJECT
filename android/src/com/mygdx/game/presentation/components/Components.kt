package com.mygdx.game.presentation.components


//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            )
    ){
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(8.dp).background(color=androidx.compose.material3.MaterialTheme.colorScheme.surface)
        ) {
            BackIcon()
        }
    }
}

@Composable
fun BackIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface, // Change the color as needed
        modifier = Modifier.size(24.dp)
    )
}





