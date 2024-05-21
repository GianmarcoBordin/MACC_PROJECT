package com.mygdx.game.presentation.inventory.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MergeButton(onClick: () -> Unit){

    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Filled.Merge,
            "Merge"
        )
    }
}