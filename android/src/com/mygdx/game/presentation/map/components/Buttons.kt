package com.mygdx.game.presentation.map.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LegendToggle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyBackButton(onClick: () -> Unit){
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            "Reload page"
        )
    }
}

@Composable
fun RefreshButton(onClick: () -> Unit){

    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Filled.Refresh,
            "Reload page"
        )
    }
}

@Composable
fun InfoButton(onClick: () -> Unit){

    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Filled.LegendToggle,
            "Show info card"
        )
    }

}
